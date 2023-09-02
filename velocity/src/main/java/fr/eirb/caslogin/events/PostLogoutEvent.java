package fr.eirb.caslogin.events;

import com.velocitypowered.api.proxy.Player;

public record PostLogoutEvent(Player player) {
}
