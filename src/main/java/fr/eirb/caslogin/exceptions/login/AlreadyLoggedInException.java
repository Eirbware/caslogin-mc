package fr.eirb.caslogin.exceptions.login;


import com.velocitypowered.api.proxy.Player;

public class AlreadyLoggedInException extends LoginException {

	public AlreadyLoggedInException(Player cause) {
		super("Player '" + cause.getUsername() + "'was already logged in!");
	}
}
