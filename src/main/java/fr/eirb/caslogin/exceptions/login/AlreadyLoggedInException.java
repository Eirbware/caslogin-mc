package fr.eirb.caslogin.exceptions.login;

import org.bukkit.entity.Player;

public class AlreadyLoggedInException extends LoginException {

	public AlreadyLoggedInException(Player cause) {
		super("Player '" + cause.getName() + "'was already logged in!");
	}
}
