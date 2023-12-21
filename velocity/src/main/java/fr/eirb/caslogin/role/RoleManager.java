package fr.eirb.caslogin.role;

import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.model.LoggedUser;

public interface RoleManager {
	void updateUserRoles(LoggedUser user);

	void removeUserRoles(LoggedUser loggedUser);

	void removePlayerData(Player player);
}
