package fr.eirb.caslogin.commands;

import fr.eirb.caslogin.manager.ConfigurationManager;
import fr.eirb.caslogin.manager.LoginManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CasTabCompleter implements TabCompleter {
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(commandSender.isOp())
			return switch (args[0]) {
				case "config" -> configComplete(args);
				case "admin" -> adminComplete(args);
				case "ban" -> banComplete();
				case "unban" -> unbanComplete();
				case "login" -> Collections.emptyList();
				case "logout" -> logoutComplete();
				default -> firstLevelArguments(commandSender);
			};
		else
			return switch (args[0]) {
				case "login", "logout" -> Collections.emptyList();
				default -> firstLevelArguments(commandSender);
			};

	}

	private List<String> logoutComplete() {
		return List.copyOf(LoginManager.INSTANCE.getLoggedCASAccounts());
	}

	private List<String> unbanComplete() {
		return List.copyOf(LoginManager.INSTANCE.getBannedUsers());
	}

	private List<String> banComplete() {
		return List.copyOf(LoginManager.INSTANCE.getLoggedCASAccounts());
	}

	private List<String> firstLevelArguments(CommandSender commandSender){
		List<String> ret = new ArrayList<>();
		if(commandSender.isOp())
			ret.addAll(Arrays.asList("config", "admin", "ban", "unban"));
		ret.addAll(Arrays.asList("login", "logout"));
		return ret;
	}

	private List<String> adminComplete(String[] args){
		if(args.length <= 2)
			return Arrays.asList("add", "remove");
		return switch(args[1]){
			case "add" -> adminAddComplete();
			case "remove" -> adminRemoveComplete();
			default -> Collections.emptyList();
		};
	}

	private List<String> adminRemoveComplete() {
		return List.copyOf(ConfigurationManager.INSTANCE.getAdmins());
	}

	private List<String> adminAddComplete() {
		return List.copyOf(LoginManager.INSTANCE.getLoggedCASAccounts());
	}

	private List<String> configComplete(String[] args) {
		if (args.length == 2)
			return Collections.singletonList("reload");
		return Collections.emptyList();
	}
}
