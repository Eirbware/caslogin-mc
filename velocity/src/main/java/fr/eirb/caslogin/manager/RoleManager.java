package fr.eirb.caslogin.manager;

import fr.eirb.caslogin.api.LoggedUser;

public interface RoleManager {
	void updateUserRoles(LoggedUser user);

	void removeUserRoles(LoggedUser loggedUser);
}
