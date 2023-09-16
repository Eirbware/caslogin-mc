package fr.eirb.caslogin.handlers;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.api.util.UuidUtils;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.api.LoggedUser;
import fr.eirb.caslogin.events.PostLoginEvent;
import fr.eirb.caslogin.manager.LoginManager;
import fr.eirb.common.compatfix.CasFixMessage;

public class SendForCompatFixPluginMessageHandler {
	@Subscribe(order = PostOrder.LAST)
	private void sendPluginMessageForFixes(ServerPostConnectEvent ev) {
		Player player = ev.getPlayer();
		LoginManager.getLoggedPlayer(player).ifPresent(loggedUser -> {
			ServerConnection conn = player.getCurrentServer().orElseThrow();
			System.out.println(player.getGameProfileProperties());
			GameProfile.Property property = player.getGameProfileProperties().get(0);
			CasFixMessage fixMessage = new CasFixMessage(
					UuidUtils.generateOfflinePlayerUuid(player.getUsername()),
					loggedUser.getFakeUserUUID(),
					property.getValue(),
					property.getSignature()
					);
			String message = fixMessage.toString();
			CasLogin.getINSTANCE().getLogger().info(String.format("Sending '%s' at '%s' to server '%s'", message, CasLogin.CAS_FIX_CHANNEL, conn.getServerInfo().getName()));
			conn.sendPluginMessage(CasLogin.CAS_FIX_CHANNEL, Charsets.UTF_8.encode(message).array());
		});

	}
}
