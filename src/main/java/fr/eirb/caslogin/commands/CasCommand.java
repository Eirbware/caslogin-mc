package fr.eirb.caslogin.commands;

import fr.eirb.caslogin.exceptions.configuration.AlreadyAdminException;
import fr.eirb.caslogin.exceptions.configuration.NotAdminException;
import fr.eirb.caslogin.exceptions.login.LoginAlreadyTakenException;
import fr.eirb.caslogin.exceptions.login.LoginException;
import fr.eirb.caslogin.manager.ConfigurationManager;
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
			case "ban" -> banSubCommand(commandSender, args);
			case "unban" -> unbanSubCommand(commandSender, args);
			case "config" -> configSubCommand(commandSender, args);
			case "admin" -> adminSubCommand(commandSender, args);
			case "login" -> loginSubCommand(commandSender, args);
			case "logout" -> logoutSubCommand(commandSender, args);
			default -> false;
		};
	}

	private boolean unbanSubCommand(CommandSender commandSender, String[] args) {
		return false;
	}

	private boolean banSubCommand(CommandSender commandSender, String[] args) {
		return false;
	}

	private boolean adminSubCommand(CommandSender sender, String[] args) {
		if(args.length != 3)
			return false;
		switch(args[1]){
			case "add" -> addAdmin(sender, args);
			case "remove" -> removeAdmin(sender, args);
		}
		return true;
	}

	private void addAdmin(CommandSender sender, String[] args) {
		String adminToAdd = args[2];
		try{
			ConfigurationManager.INSTANCE.addAdmin(adminToAdd);
			sender.sendMessage(MiniMessage.miniMessage().deserialize(MessagesEnum.ADD_ADMIN_SUCCESS.str, Placeholder.unparsed("user", adminToAdd)));
			OfflinePlayer playerToOp = LoginManager.INSTANCE.getLoggedPlayer(adminToAdd);
			if(playerToOp.isOnline())
				playerToOp.setOp(true);
		}catch(AlreadyAdminException ex){
			sender.sendMessage(MiniMessage.miniMessage().deserialize(MessagesEnum.ALREADY_ADMIN.str, Placeholder.unparsed("user", adminToAdd)));
		} catch (NotLoggedInException ignored) {
		}
	}

	private void removeAdmin(CommandSender sender, String[] args) {
		String adminToRemove = args[2];
		try{
			ConfigurationManager.INSTANCE.removeAdmin(adminToRemove);
			sender.sendMessage(MiniMessage.miniMessage().deserialize(MessagesEnum.REMOVE_ADMIN_SUCCESS.str, Placeholder.unparsed("user", adminToRemove)));
			OfflinePlayer playerToOp = LoginManager.INSTANCE.getLoggedPlayer(adminToRemove);
			if(playerToOp.isOnline())
				playerToOp.setOp(false);
		}catch(NotAdminException ex){
			sender.sendMessage(MiniMessage.miniMessage().deserialize(MessagesEnum.NOT_AN_ADMIN.str, Placeholder.unparsed("user", adminToRemove)));
		} catch (NotLoggedInException ignored) {
		}
	}

	private boolean loginSubCommand(CommandSender sender, String[] args){
		if (sender instanceof Player player) {
			if (args.length < 2)
				return false;
			try {
				LoginManager.INSTANCE.logPlayer(player, args[1]);
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
		if(args.length == 1) {
			if (sender instanceof Player player) {
				try {
					LoginManager.INSTANCE.logout(player);
					player.kick(MiniMessage.miniMessage().deserialize(MessagesEnum.LOGOUT_SUCCESS.str));
				} catch (NotLoggedInException ex) {
					player.sendMessage(MiniMessage.miniMessage().deserialize(MessagesEnum.NOT_LOGGED_IN.str));
				}
			} else {
				sender.sendMessage(MiniMessage.miniMessage().deserialize(MessagesEnum.PLAYER_EXCLUSIVE_COMMAND.str));
			}
		}else{
			if(!sender.isOp()) {
				sender.sendMessage(MiniMessage.miniMessage().deserialize(MessagesEnum.NOT_ENOUGH_PERMISSION.str));
				return true;
			}
			try{
				String userToKick = args[1];
				OfflinePlayer playerToKick = LoginManager.INSTANCE.getLoggedPlayer(userToKick);
				if(playerToKick.isOnline())
					((Player) playerToKick).kick(MiniMessage.miniMessage().deserialize(MessagesEnum.FORCE_LOGGED_OUT.str));
				LoginManager.INSTANCE.logout(userToKick);
				sender.sendMessage(MiniMessage.miniMessage().deserialize(MessagesEnum.LOGOUT_PLAYER.str, Placeholder.unparsed("user", userToKick)));
			} catch (NotLoggedInException e) {
				sender.sendMessage(MiniMessage.miniMessage().deserialize(MessagesEnum.PLAYER_NOT_LOGGED_IN.str));
			}
		}
		return true;
	}

	private boolean configSubCommand(CommandSender sender, String[] args) {
		if(!sender.isOp())
			sender.sendMessage(MiniMessage.miniMessage().deserialize(MessagesEnum.NOT_ENOUGH_PERMISSION.str));
		return false;
	}
}
