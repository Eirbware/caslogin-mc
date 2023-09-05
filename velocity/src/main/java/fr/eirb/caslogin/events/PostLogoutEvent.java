package fr.eirb.caslogin.events;

import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.api.LoggedUser;

public record PostLogoutEvent(LoggedUser user) {
}
