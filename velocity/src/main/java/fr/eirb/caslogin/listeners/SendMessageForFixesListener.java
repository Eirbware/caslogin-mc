package fr.eirb.caslogin.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.api.util.UuidUtils;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.model.LoggedUser;
import fr.eirb.caslogin.utils.PlayerUtils;
import fr.eirb.common.compatfix.CasFixMessage;

import java.util.UUID;

public class SendMessageForFixesListener {

	private static final ChannelIdentifier CAS_FIX_CHANNEL = MinecraftChannelIdentifier.from(fr.eirb.common.compatfix.Constants.CAS_FIX_CHANNEL);

	@Subscribe
	public void onLogin(ServerPostConnectEvent ev) {
		Player player = ev.getPlayer();
		GameProfile trueIdentity = PlayerUtils.getTrueIdentity(player);
		CasLogin.get().getLoginDatabase().get(trueIdentity.getId())
				.thenAccept(optionalLoggedUser -> {
					if (optionalLoggedUser.isEmpty())
						return;
					LoggedUser loggedUser = optionalLoggedUser.get();
					player.getCurrentServer().ifPresent(serverConnection -> {
						if (serverConnection.getServer().getServerInfo().equals(CasLogin.getEntrypointServer().getServerInfo()))
							return;
						UUID trueUUID = UuidUtils.generateOfflinePlayerUuid(trueIdentity.getName());
						serverConnection.sendPluginMessage(CAS_FIX_CHANNEL, new CasFixMessage(trueUUID, loggedUser.getFakeUserUUID(), trueIdentity.getName()).toByteArray());
					});
				})
				.exceptionally(throwable -> {
					throwable.printStackTrace();
					return null;
				});

	}
}
