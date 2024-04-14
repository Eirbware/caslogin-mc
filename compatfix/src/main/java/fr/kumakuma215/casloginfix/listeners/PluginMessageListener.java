package fr.kumakuma215.casloginfix.listeners;

import com.google.common.base.Charsets;
import fr.eirb.common.compatfix.CasFixMessage;
import fr.kumakuma215.casloginfix.CasLoginFix;
import fr.kumakuma215.casloginfix.FakePlayer;
import fr.kumakuma215.casloginfix.config.ConfigurationManager;
import fr.kumakuma215.casloginfix.exceptions.NoFakePlayerException;
import fr.kumakuma215.casloginfix.utils.SkinUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class PluginMessageListener implements org.bukkit.plugin.messaging.PluginMessageListener {

	private void applySkin(Player player, FakePlayer fp, boolean hasFailed){
		String url;
		if(!hasFailed)
			url = String.format("%s/merge?user=%s&accessory=%s", ConfigurationManager.getSkinApiUrl(), fp.trueName(), fp.getAccessory());
		else
			url = String.format("%s/merge?url=%s&accessory=%s", ConfigurationManager.getSkinApiUrl(), ConfigurationManager.getSteveSkinUrl(), fp.getAccessory());
		SkinUtils.runAsyncSetSkin(player, url, unused -> {}, unused -> {
			if(!hasFailed)
				CasLoginFix.INSTANCE.getLogger().info("Applying skin failed with true username. Defaulting to steve's skin");
			applySkin(player, fp, true);
		});
	}

	@Override
	public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
		ByteBuffer buff = ByteBuffer.wrap(message);
		String result = String.valueOf(Charsets.UTF_8.decode(buff));
		CasFixMessage casFixMessage = new CasFixMessage(result);
		CasLoginFix.INSTANCE.getLogger().info("Received fix message : '" + casFixMessage + "'");
		FakePlayer fp = new FakePlayer(casFixMessage);
		CasLoginFix.getFakePlayerEntriesManager().registerPlayer(fp, casFixMessage.getFalseUUID());
		if (!CasLoginFix.INSTANCE.hasSkinRestorer())
			return;
		if (player.getPlayerProfile().getTextures().isEmpty()) {
			CasLoginFix.INSTANCE.getLogger().info("Setting default skin...");
			applySkin(player, fp, false);
		}

	}
}
