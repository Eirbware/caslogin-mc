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
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.api.model.LoggedUser;
import fr.eirb.caslogin.manager.ConfigurationManager;
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
				.then(LiteralArgumentBuilder
						.<CommandSource>literal("test")
						.executes((ctx) -> {
							System.out.println(CasLogin.getLoggedEntrypointServer().sendPluginMessage(CasLogin.CAS_FIX_CHANNEL, Charsets.UTF_8.encode("a").array()));
							return Command.SINGLE_SUCCESS;
						}))
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

					return Command.SINGLE_SUCCESS;
				});
	}

	private static ArgumentBuilder<CommandSource, ?> logoutCommand(ProxyServer proxy) {
		return LiteralArgumentBuilder
				.<CommandSource>literal("logout")
				.requires(source -> !isSourceAPlayerInLimbo(source))
				.executes(context -> {
//					if (!(context.getSource() instanceof Player player)) {
//						context.getSource().sendMessage(MiniMessage
//								.miniMessage()
//								.deserialize("<red>Console cannot use this command without arguments</red>"));
//						return 0;
//					}
//					try {
//						RegisteredServer entrypointServer = CasLogin.getEntrypointServer();
//						LoginManager.logout(player);
//						player.createConnectionRequest(entrypointServer).fireAndForget();
//					} catch (NotLoggedInException e) {
//						throw new RuntimeException(e);
//					}
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
