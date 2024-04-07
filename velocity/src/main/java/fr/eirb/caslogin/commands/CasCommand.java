package fr.eirb.caslogin.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.api.handlers.APIBanHandlerImpl;
import fr.eirb.caslogin.configuration.ConfigurationManager;
import fr.eirb.caslogin.events.LogoutEvent;
import fr.eirb.caslogin.utils.PlayerUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class CasCommand {
	public static BrigadierCommand createCasCommand(final ProxyServer proxy) {
		LiteralCommandNode<CommandSource> rootNode = LiteralArgumentBuilder
				.<CommandSource>literal("cas")
				.then(loginCommand(proxy))
				.then(logoutCommand(proxy))
				.then(banCommand(proxy))
//				.then(pardonCommand(proxy))
				.then(configCommand())
				.build();
		return new BrigadierCommand(rootNode);
	}

	private static ArgumentBuilder<CommandSource, ?> pardonCommand(ProxyServer proxy) {
		return null;
	}

	private static ArgumentBuilder<CommandSource, ?> banCommand(ProxyServer proxy) {
		return LiteralArgumentBuilder
				.<CommandSource>literal("ban")
				.requires(source -> source.hasPermission("cas.ban"))
				.then(RequiredArgumentBuilder
						.<CommandSource, String>argument("login", StringArgumentType.word())
						.suggests(suggestAllLoggedPlayers())
						.executes(context -> {
							if (!context.getSource().hasPermission("cas.ban.def")) {
								context.getSource().sendMessage(MiniMessage
										.miniMessage()
										.deserialize(ConfigurationManager.getLang("not_enough_permissions")));
								return -1;
							}
							String login = context.getArgument("login", String.class);
							CasLogin.get().getLoginDatabase().getUUIDFromUserByLogin(login)
									.thenCompose(optionalUUID -> optionalUUID.isEmpty()
											? CompletableFuture.completedFuture(Optional.empty())
											: CasLogin.get().getLoginDatabase().get(optionalUUID.get()))
									.thenCompose(optionalLoggedUser -> {
										if (optionalLoggedUser.isEmpty()) {
											context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("bitch"));
											return CompletableFuture.failedFuture(new IllegalArgumentException());
										}
										return new APIBanHandlerImpl().banUser(null, optionalLoggedUser.get().user(), null, null);
									})
									.whenComplete((unused, throwable) -> {
										if(throwable != null){
											throwable.printStackTrace();
											return;
										}
										System.out.println("Yay");
									});
							return Command.SINGLE_SUCCESS;
						})
						.then(RequiredArgumentBuilder
								.<CommandSource, Integer>argument("duration", IntegerArgumentType.integer(1))
								.requires(source -> source.hasPermission("cas.ban.temp"))
								.then(RequiredArgumentBuilder.<CommandSource, String>argument("timeUnit", StringArgumentType.word())
										.executes(context -> {
											//Temp ban
											return Command.SINGLE_SUCCESS;
										}))
						)
				);
	}

	private static ArgumentBuilder<CommandSource, ?> configCommand() {
		return LiteralArgumentBuilder
				.<CommandSource>literal("config")
				.requires(source -> source.hasPermission("cas.config"))
				.then(LiteralArgumentBuilder.<CommandSource>literal("reload")
						.requires(source -> source.hasPermission("cas.config.reload"))
						.executes(context -> {
							CasLogin.get().refresh();
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

	private static SuggestionProvider<CommandSource> suggestAllLoggedPlayers() {
		return (context, builder) -> CasLogin.get().getLoginDatabase()
				.values()
				.thenAccept(loggedUsers -> {
					for (var user : loggedUsers) {
						builder.suggest(user.user().login());
					}
				})
				.thenCompose(unused -> builder.buildFuture());
	}

	private static ArgumentBuilder<CommandSource, ?> logoutPlayerAdminCommand(ProxyServer proxy) {
		return RequiredArgumentBuilder
				.<CommandSource, String>argument("login", StringArgumentType.word())
				.requires(source -> source.hasPermission("cas.logout.force"))
				.suggests(suggestAllLoggedPlayers())
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
