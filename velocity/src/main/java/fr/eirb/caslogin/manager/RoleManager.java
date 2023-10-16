package fr.eirb.caslogin.manager;

import fr.eirb.caslogin.api.model.LoggedUser;

public interface RoleManager {
	void updateUserRoles(LoggedUser user);

	void removeUserRoles(LoggedUser loggedUser);
}
