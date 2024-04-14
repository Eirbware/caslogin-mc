package fr.eirb.caslogin.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.util.GameProfile;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.utils.PlayerUtils;

public class AutoLogoutListener {
	@Subscribe
	public void onDisconnect(DisconnectEvent ev){
		GameProfile trueIdentity = PlayerUtils.getTrueIdentity(ev.getPlayer());
		if(trueIdentity == null)
			return;
		CasLogin.get()
				.getLoginDatabase()
				.get(trueIdentity.getId())
				.thenAccept(optionalLoggedUser -> {
					if(optionalLoggedUser.isEmpty())
						return;
					CasLogin.get().getLoginHandler().logout(optionalLoggedUser.get());
				});
	}
}
