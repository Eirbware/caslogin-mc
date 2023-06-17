package fr.eirb.caslogin.events;

import com.velocitypowered.api.proxy.Player;

public class LoggedInEvent{
	private final Player player;

	public LoggedInEvent(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}
}
