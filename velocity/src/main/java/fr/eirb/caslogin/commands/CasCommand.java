package fr.eirb.caslogin.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.api.util.UuidUtils;
import fr.eirb.caslogin.api.LoggedUser;
import fr.eirb.caslogin.events.PostLoginEvent;
import fr.eirb.caslogin.exceptions.login.*;
import fr.eirb.caslogin.manager.ConfigurationManager;
import fr.eirb.caslogin.manager.LoginManager;
import fr.eirb.caslogin.utils.ApiUtils;
import fr.eirb.caslogin.utils.GameProfileUtils;
import fr.eirb.caslogin.utils.PlayerUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class CasCommand {
	public static BrigadierCommand createCasCommand(final ProxyServer proxy) {
		LiteralCommandNode<CommandSource> rootNode = LiteralArgumentBuilder
				.<CommandSource>literal("cas")
				.then(loginCommand(proxy))
				.then(configCommand())
				.build();
		return new BrigadierCommand(rootNode);
	}

	private static ArgumentBuilder<CommandSource, ?> configCommand() {
		return LiteralArgumentBuilder
				.<CommandSource>literal("config")
				.requires(source -> source.hasPermission("cas.config"))
				.then(LiteralArgumentBuilder.<CommandSource>literal("reload")
						.requires(source -> source.hasPermission("cas.config.reload"))
						.executes(context -> {
							ConfigurationManager.reloadConfig();
							return Command.SINGLE_SUCCESS;
						})
				);
	}

	private static ArgumentBuilder<CommandSource, ?> loginCommand(ProxyServer proxy) {
		return LiteralArgumentBuilder
				.<CommandSource>literal("login")
				// Requires that the source is a player AND is on the Limbo server! Else no login!!!
				.requires(source -> {
					if (!(source instanceof Player player))
						return false;
					return PlayerUtils.isPlayerInLimbo(player);
				})
				.executes(context -> {
					Player player = (Player) context.getSource();
					player.sendMessage(MiniMessage
							.miniMessage()
							.deserialize(String.format(ConfigurationManager.getLang("user.login.url_message"), ApiUtils.getLoginUrl(player))));

					return Command.SINGLE_SUCCESS;
				})
				.then(RequiredArgumentBuilder
						.<CommandSource, String>argument("authCode", StringArgumentType.word())
						.executes(context -> {
							if (!(context.getSource() instanceof Player player))
								return -1;
							String authCode = context.getArgument("authCode", String.class);
							try {
								LoggedUser loggedUser = LoginManager.logPlayer(player, authCode);
								assert loggedUser != null;
								player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.login.success")));
								GameProfile prof = player.getGameProfile();
								GameProfile oldProf = GameProfileUtils.cloneGameProfile(prof);
								GameProfileUtils.setName(prof, loggedUser.getUser().getLogin());
								GameProfileUtils.setUUID(prof, loggedUser.getFakeUserUUID());
								RegisteredServer loggedServer = proxy.getServer(ConfigurationManager.getLoggedServer()).orElseThrow();
								player.createConnectionRequest(loggedServer).connect()
										.thenAccept((r) -> {
											GameProfileUtils.setToGameProfile(prof, oldProf);
											if(!r.isSuccessful()){
												if(r.getReasonComponent().isEmpty())
													player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.user_banned_no_reason")));
												else
													player.sendMessage(MiniMessage
															.miniMessage()
															.deserialize(ConfigurationManager.getLang("user.errors.user_banned"))
															.append(r.getReasonComponent().get()));
												try {
													LoginManager.logout(player);
												} catch (NotLoggedInException ignored) {}
											}else{
												proxy.getEventManager().fireAndForget(new PostLoginEvent(player, loggedUser));
											}
										});
							} catch (LoginAlreadyTakenException e) {
								player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.login_taken")));
							} catch (InvalidAuthCodeException e) {
								player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.invalid_auth_code")));
							} catch (AuthCodeExpiredException e) {
								player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.auth_code_expired")));
							} catch (InvalidTokenException e) {
								player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.invalid_token")));
							} catch (NoAuthCodeForUuidException e) {
								player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.no_auth_code_for_uuid")));
							}
							return Command.SINGLE_SUCCESS;
						})
				);
	}


}
