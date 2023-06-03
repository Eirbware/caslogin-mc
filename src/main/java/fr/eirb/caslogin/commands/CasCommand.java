package fr.eirb.caslogin.commands;

import fr.eirb.caslogin.exceptions.configuration.AlreadyAdminException;
import fr.eirb.caslogin.exceptions.configuration.NotAdminException;
import fr.eirb.caslogin.exceptions.login.*;
import fr.eirb.caslogin.manager.ConfigurationManager;
import fr.eirb.caslogin.manager.LoginManager;
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

	private boolean unbanSubCommand(CommandSender sender, String[] args) {
		if (args.length < 2)
			return false;
		String userToUnban = args[1];
		try {
			LoginManager.INSTANCE.unbanUser(userToUnban);
			sender.sendMessage(MiniMessage
					.miniMessage()
					.deserialize(ConfigurationManager.getLang("admin.unban"),
							Placeholder.unparsed("user", userToUnban)));
		} catch (NotBannedException e) {
			sender.sendMessage(MiniMessage
					.miniMessage()
					.deserialize(ConfigurationManager.getLang("admin.errors.not_banned"),
							Placeholder.unparsed("user", userToUnban)));
		}
		return true;
	}

	private boolean banSubCommand(CommandSender sender, String[] args) {
		if (args.length < 2)
			return false;
		String userToBan = args[1];
		try {
			LoginManager.INSTANCE.banUser(userToBan);
			sender.sendMessage(MiniMessage
					.miniMessage()
					.deserialize(ConfigurationManager.getLang("admin.ban"),
							Placeholder.unparsed("user", userToBan)));
		} catch (AlreadyBannedException e) {
			sender.sendMessage(MiniMessage
					.miniMessage()
					.deserialize(ConfigurationManager.getLang("admin.errors.already_banned"),
							Placeholder.unparsed("user", userToBan)));
		}
		try {
			OfflinePlayer playerFromLogin = LoginManager.INSTANCE.getLoggedPlayer(userToBan);
			if (playerFromLogin.isOnline())
				((Player) playerFromLogin).kick(MiniMessage
						.miniMessage()
						.deserialize(ConfigurationManager.getLang("user.banned")));
		} catch (NotLoggedInException ignored) {
		}
		return true;
	}

	private boolean adminSubCommand(CommandSender sender, String[] args) {
		if (args.length != 3)
			return false;
		switch (args[1]) {
			case "add" -> addAdmin(sender, args);
			case "remove" -> removeAdmin(sender, args);
		}
		return true;
	}

	private void addAdmin(CommandSender sender, String[] args) {
		String adminToAdd = args[2];
		try {
			ConfigurationManager.addAdmin(adminToAdd);
			sender.sendMessage(MiniMessage
					.miniMessage()
					.deserialize(ConfigurationManager.getLang("admin.add_admin"),
							Placeholder.unparsed("user", adminToAdd)));
			OfflinePlayer playerToOp = LoginManager.INSTANCE.getLoggedPlayer(adminToAdd);
			if (playerToOp.isOnline())
				playerToOp.setOp(true);
		} catch (AlreadyAdminException ex) {
			sender.sendMessage(MiniMessage
					.miniMessage()
					.deserialize(ConfigurationManager.getLang("admin.errors.already_admin"),
							Placeholder.unparsed("user", adminToAdd)));
		} catch (NotLoggedInException ignored) {
		}
	}

	private void removeAdmin(CommandSender sender, String[] args) {
		String adminToRemove = args[2];
		try {
			ConfigurationManager.removeAdmin(adminToRemove);
			sender.sendMessage(MiniMessage
					.miniMessage()
					.deserialize(ConfigurationManager.getLang("admin.remove_admin"),
							Placeholder.unparsed("user", adminToRemove)));
			OfflinePlayer playerToOp = LoginManager.INSTANCE.getLoggedPlayer(adminToRemove);
			if (playerToOp.isOnline())
				playerToOp.setOp(false);
		} catch (NotAdminException ex) {
			sender.sendMessage(MiniMessage
					.miniMessage()
					.deserialize("admin.errors.not_admin",
							Placeholder.unparsed("user", adminToRemove)));
		} catch (NotLoggedInException ignored) {
		}
	}

	private boolean loginSubCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player player) {
			if (args.length < 2)
				return false;
			try {
				LoginManager.INSTANCE.logPlayer(player, args[1]);
				for (PotionEffectType type : PotionEffectType.values()) {
					player.removePotionEffect(type);
				}
				player.kick(MiniMessage
						.miniMessage()
						.deserialize(ConfigurationManager.getLang("user.login.success")));

			} catch (LoginAlreadyTakenException e) {
				player.kick(MiniMessage
						.miniMessage()
						.deserialize(ConfigurationManager.getLang("user.errors.login_taken")));
				return true;
			} catch (AlreadyLoggedInException e) {
				player.sendMessage(MiniMessage
						.miniMessage()
						.deserialize(ConfigurationManager.getLang("user.errors.already_logged_in")));
				return true;
			} catch (LoginException e) {
				throw new RuntimeException(e);
			}
		} else {
			sender.sendMessage(MiniMessage
					.miniMessage()
					.deserialize("player_exclusive"));
		}
		return true;
	}

	private boolean logoutSubCommand(CommandSender sender, String[] args) {
		if (args.length == 1) {
			if (sender instanceof Player player) {
				try {
					LoginManager.INSTANCE.logout(player);
					player.kick(MiniMessage
							.miniMessage()
							.deserialize(ConfigurationManager.getLang("user.logout.success")));
				} catch (NotLoggedInException ex) {
					player.sendMessage(MiniMessage
							.miniMessage()
							.deserialize(ConfigurationManager.getLang("user.errors.not_logged_in")));
				}
			} else {
				sender.sendMessage(MiniMessage
						.miniMessage()
						.deserialize(ConfigurationManager.getLang("player_exclusive")));
			}
		} else {
			if (!sender.isOp()) {
				sender.sendMessage(MiniMessage
						.miniMessage()
						.deserialize(ConfigurationManager.getLang("not_enough_permissions")));
				return true;
			}
			try {
				String userToKick = args[1];
				OfflinePlayer playerToKick = LoginManager.INSTANCE.getLoggedPlayer(userToKick);
				if (playerToKick.isOnline())
					((Player) playerToKick).kick(MiniMessage
							.miniMessage()
							.deserialize(ConfigurationManager.getLang("user.logout.force")));
				LoginManager.INSTANCE.logout(userToKick);
				sender.sendMessage(MiniMessage
						.miniMessage()
						.deserialize(ConfigurationManager.getLang("admin.logout"),
								Placeholder.unparsed("user", userToKick)));
			} catch (NotLoggedInException e) {
				sender.sendMessage(MiniMessage
						.miniMessage()
						.deserialize(ConfigurationManager.getLang("admin.not_logged_in")));
			}
		}
		return true;
	}

	private boolean configSubCommand(CommandSender sender, String[] args) {
		if (!sender.isOp())
			sender.sendMessage(MiniMessage
					.miniMessage()
					.deserialize(ConfigurationManager.getLang("not_enough_permissions")));
		return false;
	}
}
