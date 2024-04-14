package fr.kumakuma215.casloginfix.utils;

import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.exception.MineSkinException;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.SkinApplier;
import net.skinsrestorer.api.storage.PlayerStorage;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SkinUtils {
	public static void runAsyncSetSkin(Player player, String skinDataUrl, Consumer<Void> onSuccess, Consumer<Void> onFailure){
		CompletableFuture.runAsync(() -> {
			SkinsRestorer api = SkinsRestorerProvider.get();
			PlayerStorage playerStorage = api.getPlayerStorage();
			SkinApplier<Player> skinApplier = api.getSkinApplier(Player.class);
			try {
				Optional<InputDataResult> optional = api.getSkinStorage().findOrCreateSkinData(skinDataUrl);
				if (optional.isEmpty()) {
					onFailure.accept(null);
					return;
				}
				playerStorage.setSkinIdOfPlayer(player.getUniqueId(), optional.get().getIdentifier());
				skinApplier.applySkin(player, optional.get().getProperty());
				onSuccess.accept(null);
			} catch (DataRequestException | MineSkinException e) {
				onFailure.accept(null);
			}
		});
	}
}
