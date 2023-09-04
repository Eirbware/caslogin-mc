package fr.kumakuma215.proxyutils.commands;

import com.mojang.brigadier.Command;
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
import fr.eirb.caslogin.api.LoggedUser;
import fr.eirb.caslogin.manager.LoginManager;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Optional;

public final class SendCommand {
	public static BrigadierCommand createSendCommand(ProxyServer proxy) {
		LiteralCommandNode<CommandSource> rootNode = LiteralArgumentBuilder
				.<CommandSource>literal("send")
				.then(sendPlayer(proxy))
				.build();
		return new BrigadierCommand(rootNode);
	}

	private static ArgumentBuilder<CommandSource, ?> sendPlayer(ProxyServer proxy) {
		return RequiredArgumentBuilder
				.<CommandSource, String>argument("login", StringArgumentType.word())
				.suggests((ctx, builder) -> {
					for (var user : LoginManager.getLoggedUsers()) {
						builder.suggest(user.getUser().getLogin());
					}
					return builder.buildFuture();
				})
				.then(RequiredArgumentBuilder
						.<CommandSource, String>argument("server", StringArgumentType.word())
						.suggests((ctx, builder) -> {
							for (var server : proxy.getAllServers()) {
								builder.suggest(server.getServerInfo().getName());
							}
							return builder.buildFuture();
						})
						.executes(ctx -> {
							String providedLogin = ctx.getArgument("login", String.class);
							String providedServerName = ctx.getArgument("server", String.class);
							Optional<LoggedUser> optionalUser = LoginManager.getLoggedUserByLogin(providedLogin);
							Optional<RegisteredServer> optionalServer = proxy.getServer(providedServerName);
							if(optionalUser.isEmpty()) {
								ctx.getSource().sendMessage(MiniMessage
										.miniMessage()
										.deserialize(String.format("<red>Could not find user <gold>%s</gold>. Are they logged on?</red>", providedLogin)));
								return 0;
							}
							if(optionalServer.isEmpty()){
								ctx.getSource().sendMessage(MiniMessage
										.miniMessage()
										.deserialize(String.format("<red>Could not find server <gold>%s</gold>.</red>", providedServerName)));
								return 0;
							}
							LoggedUser user = optionalUser.get();
							RegisteredServer server = optionalServer.get();
							Player loggedPlayer = proxy.getPlayer(user.getUuid()).orElseThrow();
							loggedPlayer.createConnectionRequest(server).fireAndForget();
							ctx.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<green>Tried to send user <gold>%s</gold> to server <gold>%s</gold></green>"));
							return Command.SINGLE_SUCCESS;
						})
						.build()
				);

	}
}
