package fr.kumakuma215.casloginfix.listeners;

import fr.kumakuma215.casloginfix.CasLoginFix;
import fr.kumakuma215.casloginfix.FakePlayer;
import fr.kumakuma215.casloginfix.exceptions.NoFakePlayerException;
import net.skinsrestorer.api.event.SkinApplyEvent;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class SkinApplyEventListener implements Consumer<SkinApplyEvent> {

	@Override
	public void accept(SkinApplyEvent ev) {
		Player player = ev.getPlayer(Player.class);
		if(!player.isOnline())
			return;
		FakePlayer fakePlayer = CasLoginFix.getFakePlayerEntriesManager().getFakePlayer(player);
		if(fakePlayer == null)
			return;
		fakePlayer.setTextureSignature(ev.getProperty().getSignature());
		fakePlayer.setTextureValue(ev.getProperty().getValue());
		try {
			CasLoginFix.getFakePlayerEntriesManager().refreshSkin(player);
		} catch (NoFakePlayerException e) {
			throw new RuntimeException(e);
		}
	}
}
