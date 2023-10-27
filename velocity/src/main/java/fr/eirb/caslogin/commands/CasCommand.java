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
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.configuration.ConfigurationManager;
import fr.eirb.caslogin.events.LogoutEvent;
import fr.eirb.caslogin.utils.PlayerUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.Optional;
import java.util.concurrent.CompletionException;

public final class CasCommand {
	public static BrigadierCommand createCasCommand(final ProxyServer proxy) {
		LiteralCommandNode<CommandSource> rootNode = LiteralArgumentBuilder
				.<CommandSource>literal("cas")
				.then(loginCommand(proxy))
				.then(logoutCommand(proxy))
				.then(configCommand())
				.then(LiteralArgumentBuilder.
						<CommandSource>literal("test")
						.executes(context -> {
							System.out.println(proxy.getPlayer("skhalifa"));
							return 1;
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
								proxy.getEventManager().fire(new LogoutEvent(player, loggedUser));
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
				.suggests(((context, builder) -> CasLogin.get().getLoginDatabase()
						.values()
						.thenAccept(loggedUsers -> {
							for (var user : loggedUsers) {
								builder.suggest(user.getUser().getLogin());
							}
						})
						.thenCompose(unused -> builder.buildFuture())))
				.executes(ctx -> {
					String inputtedLogin = ctx.getArgument("login", String.class);
					CasLogin.get().getLoginDatabase()
							.getUUIDFromUserByLogin(inputtedLogin)
							.thenCompose(optionalUUID -> {
								if (optionalUUID.isEmpty()) {
									ctx.getSource().sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("admin.errors.not_logged_in")));
									throw new CompletionException(null);
								}
								return CasLogin.get().getLoginDatabase().get(optionalUUID.get()).thenApply(Optional::get);
							})
							.thenAccept(loggedUser -> {
								CasLogin.get().getLoginHandler().logout(loggedUser)
										.thenAccept(loggedUser1 -> {
											Optional<Player> optionalPlayer = proxy.getPlayer(loggedUser1.getFakeUserUUID());
											CasLogin.get().getProxy().getEventManager().fire(new LogoutEvent(optionalPlayer.orElse(null), loggedUser1))
													.thenAccept(unused -> {
														optionalPlayer.ifPresent(player -> {
															PlayerUtils.restoreGameProfile(player);
															player.disconnect(MiniMessage
																	.miniMessage()
																	.deserialize(ConfigurationManager.getLang("user.logout.force")));
														});
														ctx.getSource().sendMessage(MiniMessage
																.miniMessage()
																.deserialize(ConfigurationManager.getLang("admin.logout"), Placeholder.unparsed("user", inputtedLogin)));
													});
										});
							})
							.exceptionally(throwable -> {
								ctx.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<red>Internal error</red>"));
								return null;
							});
					return Command.SINGLE_SUCCESS;
				});
	}

	private static boolean isSourceAPlayerInLimbo(CommandSource source) {
		if (!(source instanceof Player player))
			return false;
		return PlayerUtils.isPlayerInLimbo(player);

	}

}
