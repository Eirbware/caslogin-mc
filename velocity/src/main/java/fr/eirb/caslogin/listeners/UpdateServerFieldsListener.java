package fr.eirb.caslogin.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.api.model.LoggedUser;
import fr.eirb.caslogin.events.LoginEvent;
import fr.eirb.caslogin.events.LogoutEvent;
import fr.eirb.caslogin.utils.PlayerUtils;
import fr.eirb.caslogin.utils.ProxyUtils;

public class UpdateServerFieldsListener {
	@Subscribe
	public void onLogin(LoginEvent ev){
		ProxyUtils.addLoggedUserToProxy(CasLogin.get().getProxy(), ev.player(), ev.loggedUser());
	}

	@Subscribe
	public void onLogout(LogoutEvent ev){
		ProxyUtils.removeLoggedUserFromProxy(CasLogin.get().getProxy(), ev.player(), ev.loggedUser());
	}

	@Subscribe
	public void onDisconnect(DisconnectEvent ev){
		Player player = ev.getPlayer();
		CasLogin.get().getLoginDatabase()
				.get(PlayerUtils.getTrueIdentity(player).getId())
				.thenAccept(optionalLoggedUser -> {
					if(optionalLoggedUser.isEmpty())
						return;
					LoggedUser loggedUser = optionalLoggedUser.get();
					ProxyUtils.removeLoggedUserFromProxy(CasLogin.get().getProxy(), player, loggedUser);
				})
				.join();
	}
}
