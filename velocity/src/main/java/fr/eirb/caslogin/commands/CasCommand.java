package fr.eirb.caslogin.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.util.GameProfile;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.api.LoggedUser;
import fr.eirb.caslogin.events.PostLoginEvent;
import fr.eirb.caslogin.exceptions.login.*;
import fr.eirb.caslogin.manager.ConfigurationManager;
import fr.eirb.caslogin.manager.LoginManager;
import fr.eirb.caslogin.utils.ApiUtils;
import fr.eirb.caslogin.utils.GameProfileUtils;
import fr.eirb.caslogin.utils.PlayerUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.function.Consumer;

public final class CasCommand {
	public static BrigadierCommand createCasCommand(final ProxyServer proxy) {
		LiteralCommandNode<CommandSource> rootNode = LiteralArgumentBuilder
				.<CommandSource>literal("cas")
				.then(loginCommand(proxy))
				.then(logoutCommand(proxy))
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
				.requires(CasCommand::isSourceAPlayerInLimbo)
				.executes(context -> {
					Player player = (Player) context.getSource();
					String loginUrl = null;
					try {
						loginUrl = ApiUtils.getLoginUrl(player);
					} catch (CouldNotGenerateCSRFTokenException e) {
						player.sendMessage(MiniMessage
								.miniMessage()
								.deserialize(ConfigurationManager.getLang("user.errors.could_not_generate_csrf")));
						return 0;
					}
					player.sendMessage(MiniMessage
							.miniMessage()
							.deserialize(String.format(ConfigurationManager.getLang("user.login.url_message"), loginUrl)));
					LoginManager.pollLogin(player, 300, 3)
							.thenAccept(loginPlayer(player, proxy));
					return Command.SINGLE_SUCCESS;
				});
	}

	private static ArgumentBuilder<CommandSource, ?> logoutCommand(ProxyServer proxy) {
		return LiteralArgumentBuilder
				.<CommandSource>literal("logout")
				.requires(source -> (source instanceof Player) && !isSourceAPlayerInLimbo(source))
				.executes(context -> {
					Player player = (Player) context.getSource();
					try {
						RegisteredServer limboServer = proxy.getServer(ConfigurationManager.getLimboServerName()).orElseThrow();
						LoginManager.logout(player);
						player.createConnectionRequest(limboServer).fireAndForget();
					} catch (NotLoggedInException e) {
						throw new RuntimeException(e);
					}
					return Command.SINGLE_SUCCESS;
				});
	}

	private static boolean isSourceAPlayerInLimbo(CommandSource source) {
		if (!(source instanceof Player player))
			return false;
		return PlayerUtils.isPlayerInLimbo(player);

	}

	private static Consumer<LoggedUser> loginPlayer(Player player, ProxyServer proxy) {
		return (loggedUser) -> {
			if (loggedUser == null) {
				player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("errors.login_timeout")));
				return;
			}
			LoginManager.moveLoggedPlayer(player, proxy, loggedUser);
		};
	}

//	.then(RequiredArgumentBuilder
//				  .<CommandSource, String>argument("authCode", StringArgumentType.word())
//			.executes(context -> {
//		if (!(context.getSource() instanceof Player player))
//			return -1;
//		String authCode = context.getArgument("authCode", String.class);
//		try {
//			LoggedUser loggedUser = LoginManager.logPlayer(player, authCode);
//			assert loggedUser != null;
//		} catch (LoginAlreadyTakenException e) {
//			player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.login_taken")));
//		} catch (InvalidAuthCodeException e) {
//			player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.invalid_auth_code")));
//		} catch (AuthCodeExpiredException e) {
//			player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.auth_code_expired")));
//		} catch (InvalidTokenException e) {
//			player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.invalid_token")));
//		} catch (NoAuthCodeForUuidException e) {
//			player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.no_auth_code_for_uuid")));
//		}
//		return Command.SINGLE_SUCCESS;
//	})

}
