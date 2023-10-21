package fr.eirb.caslogin.listeners;

import com.velocitypowered.api.event.Subscribe;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.events.LoginEvent;
import fr.eirb.caslogin.events.LogoutEvent;

public class RoleUpdaterListener {
	@Subscribe
	public void onLogin(LoginEvent ev){
		CasLogin.get().getRoleManager().updateUserRoles(ev.loggedUser());
	}

	@Subscribe
	public void onLogout(LogoutEvent ev){
		CasLogin.get().getRoleManager().removeUserRoles(ev.loggedUser());
	}
}
