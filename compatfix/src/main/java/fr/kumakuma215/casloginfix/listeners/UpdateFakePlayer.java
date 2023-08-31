package fr.kumakuma215.casloginfix.listeners;

import fr.kumakuma215.casloginfix.CasLoginFix;
import fr.kumakuma215.casloginfix.exceptions.NoFakePlayerException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class UpdateFakePlayer implements Listener {
	@EventHandler
	public void onGamemodeChange(PlayerGameModeChangeEvent ev){
		try {
			CasLoginFix.getFakePlayerEntriesManager().updateGamemode(ev.getPlayer(), ev.getNewGameMode());
		} catch (NoFakePlayerException ignored) {
		}
	}
}
