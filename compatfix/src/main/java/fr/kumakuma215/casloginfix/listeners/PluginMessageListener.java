package fr.kumakuma215.casloginfix.listeners;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.base.Charsets;
import fr.eirb.common.compatfix.CasFixMessage;
import fr.kumakuma215.casloginfix.CasLoginFix;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class PluginMessageListener implements org.bukkit.plugin.messaging.PluginMessageListener {

	@Override
	public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
		ByteBuffer buff = ByteBuffer.wrap(message);
		String result = String.valueOf(Charsets.UTF_8.decode(buff));
		CasFixMessage casFixMessage = new CasFixMessage(result);
		CasLoginFix.getFakePlayerEntriesManager().setSkin(player, casFixMessage.getTextureValue(), casFixMessage.getTextureSignature());
		CasLoginFix.getFakePlayerEntriesManager().registerPlayer(casFixMessage.getTrueUUID(), casFixMessage.getFalseUUID());
	}
}
