package fr.eirb.caslogin.listeners;

import com.velocitypowered.api.event.Subscribe;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.events.LoginEvent;
import fr.eirb.caslogin.events.LogoutEvent;
import fr.eirb.caslogin.utils.ProxyUtils;

public class UpdateServerFieldsListener {
	@Subscribe
	public void onLogin(LoginEvent ev){
		ProxyUtils.addLoggedUserToProxy(CasLogin.get().getProxy(), ev.player(), ev.loggedUser());
	}

	@Subscribe
	public void onLogout(LogoutEvent ev){
		if(ev.player() != null) {
			ProxyUtils.removeLoggedUserFromProxy(CasLogin.get().getProxy(), ev.player(), ev.loggedUser());
		}
	}
}
