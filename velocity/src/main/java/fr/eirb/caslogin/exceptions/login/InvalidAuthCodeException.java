package fr.eirb.caslogin.exceptions.login;

import com.velocitypowered.api.proxy.Player;

public class InvalidAuthCodeException extends LoginException{
	public InvalidAuthCodeException(String authCode, Player cause) {
		super("The auth code '" + authCode + "' is not valid for the player '" + cause.getUniqueId().toString() + "'!");
	}
}
