package fr.eirb.caslogin.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.api.model.LoggedUser;
import fr.eirb.caslogin.events.LoginEvent;
import fr.eirb.caslogin.events.LogoutEvent;
import fr.eirb.caslogin.utils.PlayerUtils;

import java.util.Optional;

public class RoleUpdaterListener {
	@Subscribe
	public void onLogin(LoginEvent ev){
		CasLogin.get().getRoleManager().updateUserRoles(ev.loggedUser());
	}

	@Subscribe
	public void onLogout(LogoutEvent ev){
		CasLogin.get().getRoleManager().removeUserRoles(ev.loggedUser());
	}

	@Subscribe
	public void onDisconnect(DisconnectEvent ev){
		CasLogin.get().getRoleManager().removePlayerData(ev.getPlayer());
	}
}
