package fr.kumakuma215.casloginfix.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SetSkinCompleter implements TabCompleter {
	private final static List<String> availablesubcommands = List.of("url", "player");

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (args.length <= 1)
			return availablesubcommands;
		return Collections.emptyList();
	}
}
