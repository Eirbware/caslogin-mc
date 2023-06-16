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
import com.velocitypowered.api.util.UuidUtils;
import fr.eirb.caslogin.api.LoggedUser;
import fr.eirb.caslogin.exceptions.login.AlreadyLoggedInException;
import fr.eirb.caslogin.exceptions.login.LoginAlreadyTakenException;
import fr.eirb.caslogin.exceptions.login.LoginException;
import fr.eirb.caslogin.manager.ConfigurationManager;
import fr.eirb.caslogin.manager.LoginManager;
import fr.eirb.caslogin.utils.ApiUtils;
import fr.eirb.caslogin.utils.GameProfileUtils;
import fr.eirb.caslogin.utils.PlayerUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public final class CasCommand {
	public static BrigadierCommand createCasCommand(final ProxyServer proxy) {
		LiteralCommandNode<CommandSource> rootNode = LiteralArgumentBuilder
				.<CommandSource>literal("cas")
				.then(loginCommand(proxy))
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
				});
	}


}
