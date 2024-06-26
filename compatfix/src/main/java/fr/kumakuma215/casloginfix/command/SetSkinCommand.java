package fr.kumakuma215.casloginfix.command;

import fr.kumakuma215.casloginfix.CasLoginFix;
import fr.kumakuma215.casloginfix.FakePlayer;
import fr.kumakuma215.casloginfix.config.ConfigurationManager;
import fr.kumakuma215.casloginfix.utils.SkinUtils;
import fr.kumakuma215.casloginfix.utils.ValidationUtil;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.exception.MineSkinException;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.SkinApplier;
import net.skinsrestorer.api.storage.PlayerStorage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SetSkinCommand implements CommandExecutor {

	private static final ConcurrentHashMap<UUID, Date> lastUsedCommand = new ConcurrentHashMap<>();

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player player)) {
			sender.sendRichMessage("<red>This command is only for players");
			return true;
		}
		if (CasLoginFix.getFakePlayerEntriesManager().getFakePlayer(player) == null) {
			sender.sendRichMessage("<red>Internal error! No fake player associated. Please report to an admin!!!");
			return true;
		}
		if (args.length != 2) {
			sender.sendRichMessage("<red>This command only accepts 2 arguments!");
			return false;
		}
		if(lastUsedCommand.containsKey(player.getUniqueId())){
			var then = lastUsedCommand.get(player.getUniqueId());
			var now = new Date();
			Duration diff = Duration.between(then.toInstant(), now.toInstant());
			Duration cooldown = Duration.ofMinutes(1);
			if(diff.compareTo(cooldown) < 0) {
				Duration remaining = cooldown.minus(diff);
				player.sendRichMessage("<red>Command on cooldown. Please wait " + remaining.toMinutes() + "m" + remaining.toSecondsPart() + "s");
				return true;
			}
		}
		if (args[0].equals("player")) {
			return playerSubCommand(player, command, label, args);
		}
		if (args[0].equals("url")) {
			return urlSubCommand(player, command, label, args);
		}
		sender.sendRichMessage("<red>Unknown first argument");
		return false;
	}

	private boolean urlSubCommand(Player player, Command command, String label, String[] args) {
		String urlArg = args[1];
		if (!ValidationUtil.validSkinUrl(urlArg)) {
			player.sendRichMessage("<red>Invalid URL format!");
			return false;
		}
		urlArg = URLEncoder.encode(urlArg, StandardCharsets.UTF_8);
		FakePlayer fp = CasLoginFix.getFakePlayerEntriesManager().getFakePlayer(player);
		String url = String.format("%s/merge?url=%s&accessory=%s", ConfigurationManager.getSkinApiUrl(), urlArg, fp.getAccessory());
		player.sendRichMessage("<gray>Setting your skin...");
		SkinUtils.runAsyncSetSkin(
				player,
				url,
				unused -> {
					lastUsedCommand.put(player.getUniqueId(), new Date());
					player.sendRichMessage("<green>Your skin has been set!");
					},
				unused -> player.sendRichMessage("<red>Couldn't fetch skin. Maybe it's not a valid URL or it took too long to fetch...")
		);
		return true;
	}

	private boolean playerSubCommand(Player player, Command command, String label, String[] args) {
		String playerNameArg = args[1];
		if (ValidationUtil.invalidMinecraftUsername(playerNameArg)) {
			player.sendRichMessage("<red>Invalid player name");
		}
		FakePlayer fp = CasLoginFix.getFakePlayerEntriesManager().getFakePlayer(player);
		String url = String.format("%s/merge?user=%s&accessory=%s", ConfigurationManager.getSkinApiUrl(), playerNameArg, fp.getAccessory());
		player.sendRichMessage("<gray>Setting your skin...");

		SkinUtils.runAsyncSetSkin(
				player,
				url,
				unused -> {
					lastUsedCommand.put(player.getUniqueId(), new Date());
					player.sendRichMessage("<green>Your skin has been set!");
				},
				unused -> player.sendRichMessage("<red>Couldn't fetch skin. Maybe it's not a valid minecraft name or it took too long to fetch...")
		);
		return true;
	}
}
