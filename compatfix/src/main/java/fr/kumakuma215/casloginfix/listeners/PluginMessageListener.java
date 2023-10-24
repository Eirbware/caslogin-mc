package fr.kumakuma215.casloginfix.listeners;

import com.google.common.base.Charsets;
import fr.eirb.common.compatfix.CasFixMessage;
import fr.kumakuma215.casloginfix.CasLoginFix;
import fr.kumakuma215.casloginfix.FakePlayer;
import fr.kumakuma215.casloginfix.exceptions.NoFakePlayerException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class PluginMessageListener implements org.bukkit.plugin.messaging.PluginMessageListener {

	@Override
	public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
		ByteBuffer buff = ByteBuffer.wrap(message);
		String result = String.valueOf(Charsets.UTF_8.decode(buff));
		CasFixMessage casFixMessage = new CasFixMessage(result);
		CasLoginFix.getFakePlayerEntriesManager().registerPlayer(new FakePlayer(casFixMessage), casFixMessage.getFalseUUID());
		if(!CasLoginFix.INSTANCE.hasSkinRestorer())
			return;
		try {
			CasLoginFix.getFakePlayerEntriesManager().refreshSkin(casFixMessage.getFalseUUID());
		} catch (NoFakePlayerException ignored) {
		}
	}
}
