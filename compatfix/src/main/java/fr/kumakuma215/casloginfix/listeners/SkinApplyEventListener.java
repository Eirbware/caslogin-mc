package fr.kumakuma215.casloginfix.listeners;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import fr.kumakuma215.casloginfix.CasLoginFix;
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
		try {
			var fakePlayer = CasLoginFix.getFakePlayerEntriesManager().getFakePlayer(player);
			var skinProperty = ev.getProperty();
			fakePlayer.setTexture(skinProperty.getValue(), skinProperty.getSignature());
			CasLoginFix.getFakePlayerEntriesManager().refreshSkin(player);
		} catch (NoFakePlayerException e) {
			throw new RuntimeException(e);
		}
	}
}
