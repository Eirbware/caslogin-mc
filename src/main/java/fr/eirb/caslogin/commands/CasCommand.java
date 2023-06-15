package fr.eirb.caslogin.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.eirb.caslogin.utils.PlayerUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class CasCommand {
	public static BrigadierCommand createCasCommand(final ProxyServer proxy){
		LiteralCommandNode<CommandSource> rootNode = LiteralArgumentBuilder
				.<CommandSource>literal("cas")
				.then(loginCommand())
				.then(configCommand())
				.executes(context -> {
					context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("OI"));
					return Command.SINGLE_SUCCESS;
				})
				.build();
		return new BrigadierCommand(rootNode);
	}

	private static ArgumentBuilder<CommandSource, ?> configCommand() {
		return LiteralArgumentBuilder
				.<CommandSource>literal("config")
				.requires(source -> source.hasPermission("cas.config"))
				.executes(ctx -> {
					ctx.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<red>bar</red>"));
					return Command.SINGLE_SUCCESS;
				});
	}

	private static ArgumentBuilder<CommandSource, ?	> loginCommand() {
		return LiteralArgumentBuilder
				.<CommandSource>literal("login")
				// Requires that the source is a player AND is on the Limbo server! Else no login!!!
				.requires(source -> {
					if(!(source instanceof Player player))
						return false;
					return PlayerUtils.isPlayerInLimbo(player);
				})
				.executes(context -> {
					context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<green>foo</green>"));
					return Command.SINGLE_SUCCESS;
				});
	}


}
