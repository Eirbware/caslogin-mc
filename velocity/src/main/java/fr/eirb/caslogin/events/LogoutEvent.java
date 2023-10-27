package fr.eirb.caslogin.events;

import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.api.model.LoggedUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired whenever a user logs out.
 * @param loggedUser
 */
public record LogoutEvent(@Nullable Player player, @NotNull LoggedUser loggedUser) {
}
