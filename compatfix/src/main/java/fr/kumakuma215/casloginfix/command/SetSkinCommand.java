package fr.kumakuma215.casloginfix.command;

import fr.kumakuma215.casloginfix.CasLoginFix;
import fr.kumakuma215.casloginfix.FakePlayer;
import fr.kumakuma215.casloginfix.config.ConfigurationManager;
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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SetSkinCommand implements CommandExecutor {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player player)) {
			sender.sendRichMessage("<red>This command is only for players");
			return false;
		}
		if(CasLoginFix.getFakePlayerEntriesManager().getFakePlayer(player) == null){
			sender.sendRichMessage("<red>Internal error! No fake player associated. Please report to an admin!!!");
			return false;
		}
		if (args.length != 2) {
			sender.sendRichMessage("<red>This command only accepts 2 arguments!");
			return false;
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

	private void runAsyncSetSkin(Player player, String input, String failMessage) {
		CompletableFuture.runAsync(() -> {
			SkinsRestorer api = SkinsRestorerProvider.get();
			PlayerStorage playerStorage = api.getPlayerStorage();
			SkinApplier<Player> skinApplier = api.getSkinApplier(Player.class);
			try {
				Optional<InputDataResult> optional = api.getSkinStorage().findOrCreateSkinData(input);
				if (optional.isEmpty()) {
					player.sendRichMessage(failMessage);
					return;
				}
				playerStorage.setSkinIdOfPlayer(player.getUniqueId(), optional.get().getIdentifier());
				skinApplier.applySkin(player, optional.get().getProperty());
				player.sendRichMessage("<green>Your skin has been set!");
			} catch (DataRequestException | MineSkinException e) {
				player.sendRichMessage(failMessage);
			}
		});
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
		runAsyncSetSkin(player, url, "<red>Couldn't fetch skin. Maybe it's not a valid URL or it took too long to fetch...");
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

		runAsyncSetSkin(player, url, "<red>Couldn't fetch skin. Maybe it's not a valid minecraft name or it took too long to fetch...");
		return true;
	}
}
