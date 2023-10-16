package fr.eirb.caslogin.events;

import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.api.model.LoggedUser;

public record PostLoginEvent(Player player, LoggedUser loggedUser) {


}
