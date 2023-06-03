package fr.eirb.caslogin.commands;

import fr.eirb.caslogin.exceptions.login.LoginAlreadyTakenException;
import fr.eirb.caslogin.exceptions.login.LoginException;
import fr.eirb.caslogin.utils.MessagesEnum;
import fr.eirb.caslogin.exceptions.login.AlreadyLoggedInException;
import fr.eirb.caslogin.exceptions.login.NotLoggedInException;
import fr.eirb.caslogin.manager.LoginManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class CasCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (args.length == 0)
			return false;
		return switch (args[0]) {
			case "user" -> userSubCommand(commandSender, args);
			case "config" -> configSubCommand(args);
			case "login" -> loginSubCommand(commandSender, args);
			case "logout" -> logoutSubCommand(commandSender, args);
			default -> false;
		};
	}

	private boolean loginSubCommand(CommandSender sender, String[] args){
		if (sender instanceof Player player) {
			if (args.length < 1)
				return false;
			try {
				LoginManager.INSTANCE.logPlayer(player, args[0]);
				for (PotionEffectType type : PotionEffectType.values()) {
					player.removePotionEffect(type);
				}
				player.kick(MiniMessage.miniMessage().deserialize(MessagesEnum.LOGIN_SUCCESS.str));

			} catch (LoginAlreadyTakenException e) {
				player.kick(MiniMessage.miniMessage().deserialize(MessagesEnum.LOGIN_TAKEN.str));
				return true;
			} catch (AlreadyLoggedInException e) {
				player.sendMessage(MiniMessage.miniMessage().deserialize(MessagesEnum.ALREADY_LOGGED_IN.str));
				return true;
			} catch (LoginException e) {
				throw new RuntimeException(e);
			}
		}else{
			sender.sendMessage(MiniMessage.miniMessage().deserialize(MessagesEnum.PLAYER_EXCLUSIVE_COMMAND.str));
		}
		return true;
	}

	private boolean logoutSubCommand(CommandSender sender, String[] args){
		if (sender instanceof Player player) {
			try{
				LoginManager.INSTANCE.logout(player);
				player.kick(MiniMessage.miniMessage().deserialize(MessagesEnum.LOGOUT_SUCCESS.str));
			}catch(NotLoggedInException ex){
				player.sendMessage(MiniMessage.miniMessage().deserialize(MessagesEnum.NOT_LOGGED_IN.str));
			}
		}else{
			sender.sendMessage(MiniMessage.miniMessage().deserialize(MessagesEnum.PLAYER_EXCLUSIVE_COMMAND.str));
		}
		return true;
	}

	private boolean userSubCommand(CommandSender sender, String[] args) {
		if (args.length != 3)
			return false;
		if (args[2].equals("logout")) {
			String userToKick = args[1];
			try {
				OfflinePlayer playerToKick = LoginManager.INSTANCE.getLoggedPlayer(userToKick);
				if (playerToKick.isOnline())
					((Player) playerToKick).kick(MiniMessage
							.miniMessage()
							.deserialize(MessagesEnum.FORCE_LOGGED_OUT.str));
				LoginManager.INSTANCE.logout(userToKick);
				sender.sendMessage(MiniMessage
						.miniMessage()
						.deserialize(MessagesEnum.LOGOUT_PLAYER.str, Placeholder.unparsed("user", userToKick)));
				return true;
			} catch (NotLoggedInException ex) {
				sender.sendMessage(Component.text(ex.getMessage(), Style.style(NamedTextColor.RED)));
			}
		}
		return false;
	}

	private boolean configSubCommand(String[] args) {
		return false;
	}
}
