package fr.eirb.caslogin.events;

import fr.eirb.caslogin.api.model.LoggedUser;

/**
 * This event is fired whenever a user logs out.
 * @param loggedUser
 */
public record LogoutEvent(LoggedUser loggedUser) {
	public LoggedUser getLoggedUser() {
		return loggedUser;
	}
}
