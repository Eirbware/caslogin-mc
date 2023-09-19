package fr.kumakuma215.casloginfix.listeners;

import fr.kumakuma215.casloginfix.CasLoginFix;
import fr.kumakuma215.casloginfix.exceptions.NoFakePlayerException;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class UpdateFakePlayer implements Listener {
	@EventHandler
	public void onGamemodeChange(PlayerGameModeChangeEvent ev){
		try {
			CasLoginFix.getFakePlayerEntriesManager().updateGamemode(ev.getPlayer(), ev.getNewGameMode());
		} catch (NoFakePlayerException ignored) {
		}
	}

	@EventHandler
		public void onPlayerSpawn(PlayerResourcePackStatusEvent ev) throws DataRequestException {
		if(ev.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED)
			SkinsRestorerProvider.get().getSkinApplier(Player.class).applySkin(ev.getPlayer());
	}
}
