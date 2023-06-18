package fr.kumakuma215.casloginfix.listeners;

import com.google.common.base.Charsets;
import fr.kumakuma215.casloginfix.CasLoginFix;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.UUID;

public class PluginMessageListener implements org.bukkit.plugin.messaging.PluginMessageListener {

	@Override
	public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
		ByteBuffer buff = ByteBuffer.wrap(message);
		String result = String.valueOf(Charsets.UTF_8.decode(buff));
		String[] split = result.split(":");
		if(split.length != 2)
			throw new RuntimeException("INVALID FORMAT FOR PLUGIN MESSAGE");
		UUID trueUUID = UUID.fromString(split[0].trim());
		UUID falseUUID = UUID.fromString(split[1].trim());
		CasLoginFix.getFakePlayerEntriesManager().registerPlayer(trueUUID, falseUUID);

	}
}
