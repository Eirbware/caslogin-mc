package fr.kumakuma215.proxyutils.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.eirb.caslogin.manager.LoginManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.function.Predicate;

public final class AlertCommand {
	public static BrigadierCommand createAlertCommand(final ProxyServer server) {
		LiteralCommandNode<CommandSource> rootNode = LiteralArgumentBuilder
				.<CommandSource>literal("alert")
				.then(alertLogged(server))
				.then(alertNotLogged(server))
				.then(alertAll(server))
				.build();
		return new BrigadierCommand(rootNode);
	}

	private static void sendMessageIfPredicate(Collection<Player> players, String message, Predicate<Player> predicate) {
		for (var p : players) {
			if (predicate.test(p)) {
				p.sendMessage(MiniMessage.miniMessage().deserialize("<gray>[</gray><dark_red>ALERT</dark_red><gray>]</gray> ")
						.append(MiniMessage.miniMessage().deserialize(message)));
			}
		}
	}

	private static ArgumentBuilder<CommandSource, ?> buildSubCommand(String literal, @Nullable String permission, ProxyServer server, Predicate<Player> predicate) {
		var builder = LiteralArgumentBuilder
				.<CommandSource>literal(literal);
		if (permission != null)
			builder = builder.requires(source -> source.hasPermission(permission));
		return builder
				.then(RequiredArgumentBuilder
						.<CommandSource, String>argument("message", StringArgumentType.greedyString())
						.executes(context -> {
							String message = context.getArgument("message", String.class);
							sendMessageIfPredicate(server.getAllPlayers(), message, predicate);
							return Command.SINGLE_SUCCESS;
						})
				);
	}

	private static ArgumentBuilder<CommandSource, ?> alertAll(ProxyServer server) {
		return buildSubCommand("all", "proxyutils.alert.all", server, (p) -> true);
	}

	private static ArgumentBuilder<CommandSource, ?> alertNotLogged(ProxyServer server) {
		return buildSubCommand("notlogged", "proxyutils.alert.notlogged", server, (p) -> LoginManager.getLoggedPlayer(p).isEmpty());
	}

	private static ArgumentBuilder<CommandSource, ?> alertLogged(ProxyServer server) {
		return buildSubCommand("logged", "proxyutils.alert.logged", server, (p) -> LoginManager.getLoggedPlayer(p).isPresent());
	}
}
