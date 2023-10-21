package fr.eirb.caslogin.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.events.LoginEvent;
import fr.eirb.caslogin.proxy.connection.Connector;
import fr.eirb.caslogin.utils.PlayerUtils;

public class AutoLoginListener {
	@Subscribe
	public void onJoinLimbo(ServerPostConnectEvent ev) {
		Player p = ev.getPlayer();
		p.getCurrentServer().ifPresent(serverConnection -> {
			if (serverConnection.getServer().getServerInfo().equals(CasLogin.getEntrypointServer().getServerInfo())) {
				CasLogin.get().getLoginDatabase()
						.get(PlayerUtils.getTrueIdentity(p).getId())
						.thenAccept(optionalLoggedUser -> {
							optionalLoggedUser.ifPresent(PlayerUtils.logPlayer(p));
						});
			}
		});
	}
}
