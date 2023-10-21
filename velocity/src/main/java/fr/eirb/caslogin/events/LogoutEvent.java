package fr.eirb.caslogin.events;

import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.api.model.LoggedUser;

/**
 * This event is fired whenever a user logs out.
 * @param loggedUser
 */
public record LogoutEvent(Player player, LoggedUser loggedUser) {
}
