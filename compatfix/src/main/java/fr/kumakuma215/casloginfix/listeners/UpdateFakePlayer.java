package fr.kumakuma215.casloginfix.listeners;

import fr.kumakuma215.casloginfix.CasLoginFix;
import fr.kumakuma215.casloginfix.FakePlayer;
import fr.kumakuma215.casloginfix.exceptions.NoFakePlayerException;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.connections.MineSkinAPI;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.exception.MineSkinException;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.SkinIdentifier;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.storage.SkinStorage;
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

//	private static SkinIdentifier getSkinIdOfPremiumPlayer(SkinsRestorer api, String username) throws MineSkinException, DataRequestException {
//		SkinStorage skinStorage = api.getSkinStorage();
//		InputDataResult result = skinStorage.findOrCreateSkinData(username).orElseThrow();
//		return result.getIdentifier();
//	}

//	@EventHandler
//	public void onPlayerJoin(PlayerJoinEvent ev) throws MineSkinException {
//		if(!CasLoginFix.INSTANCE.hasSkinRestorer())
//			return;
//		try {
//			FakePlayer fakePlayer = CasLoginFix.getFakePlayerEntriesManager().getFakePlayer(ev.getPlayer());
//			if(fakePlayer == null)
//				return;
//			SkinsRestorer api = SkinsRestorerProvider.get();
//			var optionalSkinProperty = api.getPlayerStorage().getSkinOfPlayer(ev.getPlayer().getUniqueId());
//			if(optionalSkinProperty.isEmpty()){
//				api.getPlayerStorage().setSkinIdOfPlayer(ev.getPlayer().getUniqueId(), getSkinIdOfPremiumPlayer(api, fakePlayer.trueName()));
//				api.getSkinApplier(Player.class).applySkin(ev.getPlayer());
//			}
//			CasLoginFix.getFakePlayerEntriesManager().refreshSkin(ev.getPlayer().getUniqueId());
//		} catch (DataRequestException | NoFakePlayerException e) {
//			throw new RuntimeException(e);
//		}
//	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent ev){
		CasLoginFix.getFakePlayerEntriesManager().deleteFakePlayer(ev.getPlayer());
	}
}
