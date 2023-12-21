package fr.eirb.caslogin.role.impl;

import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.model.LoggedUser;
import fr.eirb.caslogin.role.RoleManager;

public class DummyRoleManager implements RoleManager {
	@Override
	public void updateUserRoles(LoggedUser user) {
		// DO NOTHING
	}

	@Override
	public void removeUserRoles(LoggedUser loggedUser) {
		// DO NOTHING
	}

	@Override
	public void removePlayerData(Player player) {
		// DO NOTHING
	}
}
