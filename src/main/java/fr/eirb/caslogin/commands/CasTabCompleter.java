package fr.eirb.caslogin.commands;

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
		return switch (args[0]) {
			case "user" -> userComplete(args);
			case "config" -> configComplete(args);
			case "login", "logout" -> Collections.emptyList();
			default -> firstLevelArguments(commandSender, args);
		};

	}

	private List<String> firstLevelArguments(CommandSender commandSender, String[] args){
		List<String> ret = new ArrayList<>();
		if(commandSender.isOp())
			ret.addAll(Arrays.asList("config", "user"));
		ret.addAll(Arrays.asList("login", "logout"));
		return ret;
	}

	private List<String> userComplete(String[] args) {
		if (args.length == 2)
			return List.copyOf(LoginManager.INSTANCE.getLoggedCASAccounts());
		return userOptionsComplete(args);
	}

	private List<String> configComplete(String[] args) {
		if (args.length == 2)
			return Collections.singletonList("reload");
		return Collections.emptyList();
	}

	private List<String> userOptionsComplete(String[] args) {
		if (args.length == 3)
			return Arrays.asList("logout", "ban");
		return Collections.emptyList();
	}
}
