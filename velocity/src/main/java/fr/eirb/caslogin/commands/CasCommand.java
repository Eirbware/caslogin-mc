package fr.eirb.caslogin.commands;

import com.google.common.base.Charsets;
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
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.api.model.LoggedUser;
import fr.eirb.caslogin.configuration.ConfigurationManager;
import fr.eirb.caslogin.events.LogoutEvent;
import fr.eirb.caslogin.exceptions.login.NotLoggedInException;
import fr.eirb.caslogin.proxy.connection.Connector;
import fr.eirb.caslogin.utils.PlayerUtils;
import fr.eirb.caslogin.utils.ProxyUtils;
import net.kyori.adventure.text.Component;
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
							CasLogin.get().getLoginHandler().getLoggedUsers();
							context.getSource().sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("admin.config.reload")));
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
					if (!(context.getSource() instanceof Player player))
						return 0;
					CasLogin.get().getLoginHandler()
							.login(player)
							.thenAccept(PlayerUtils.logPlayer(player));
					return Command.SINGLE_SUCCESS;
				});
	}

	private static ArgumentBuilder<CommandSource, ?> logoutCommand(ProxyServer proxy) {
		return LiteralArgumentBuilder
				.<CommandSource>literal("logout")
				.requires(source -> !isSourceAPlayerInLimbo(source))
				.executes(context -> {
					if (!(context.getSource() instanceof Player player)) {
						context.getSource().sendMessage(MiniMessage
								.miniMessage()
								.deserialize("<red>Console cannot use this command without arguments</red>"));
						return 0;
					}
					RegisteredServer entrypointServer = CasLogin.getEntrypointServer();
					CasLogin.get().getLoginHandler()
							.logout(player)
							.thenAccept(loggedUser -> {
								proxy.getEventManager().fire(new LogoutEvent(loggedUser));
								PlayerUtils.restoreGameProfile(player);
								player.createConnectionRequest(entrypointServer).fireAndForget();
							});

					return Command.SINGLE_SUCCESS;
				})
				.then(logoutPlayerAdminCommand(proxy));
	}

	private static ArgumentBuilder<CommandSource, ?> logoutPlayerAdminCommand(ProxyServer proxy) {
		return RequiredArgumentBuilder
				.<CommandSource, String>argument("login", StringArgumentType.word())
				.requires(source -> source.hasPermission("caslogin.admin.logout.player"))
				.suggests(((context, builder) -> {
//					for (var user : LoginManager.getLoggedUsers()) {
//						builder.suggest(user.getUser().getLogin());
//					}
					return builder.buildFuture();
				}))
				.executes(ctx -> {
					String inputtedLogin = ctx.getArgument("login", String.class);
//					var optionalUser = LoginManager.getLoggedUserByLogin(inputtedLogin);
//					if (optionalUser.isEmpty()) {
//						ctx.getSource().sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("admin.errors.not_logged_in")));
//						return 0;
//					}
//					LoggedUser user = optionalUser.get();
//					try {
//						LoginManager.logout(user);
//					} catch (NotLoggedInException e) {
//						throw new IllegalStateException(e);
//					}
//					proxy.getPlayer(user.getUuid())
//							.ifPresent(player ->
//									player.disconnect(MiniMessage
//											.miniMessage()
//											.deserialize(ConfigurationManager.getLang("user.logout.force"))));
//					ctx.getSource().sendMessage(MiniMessage
//							.miniMessage()
//							.deserialize(ConfigurationManager.getLang("admin.logout"), Placeholder.unparsed("user", inputtedLogin)));
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
				return;
			}
//			LoginManager.moveLoggedPlayer(player, proxy, loggedUser);
		};
	}

}
