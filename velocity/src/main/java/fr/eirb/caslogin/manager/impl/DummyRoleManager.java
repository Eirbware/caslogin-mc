package fr.eirb.caslogin.manager.impl;

import fr.eirb.caslogin.api.model.LoggedUser;
import fr.eirb.caslogin.manager.RoleManager;

public class DummyRoleManager implements RoleManager {
	@Override
	public void updateUserRoles(LoggedUser user) {
		// DO NOTHING
	}

	@Override
	public void removeUserRoles(LoggedUser loggedUser) {
		// DO NOTHING
	}
}
