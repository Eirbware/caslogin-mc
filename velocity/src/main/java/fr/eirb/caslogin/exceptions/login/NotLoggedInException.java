package fr.eirb.caslogin.exceptions.login;


import com.velocitypowered.api.proxy.Player;

public class NotLoggedInException extends LoginException{

	public NotLoggedInException(Player cause){
		super("Player '" + cause.getUsername() + "'is not logged in!");
	}

	public NotLoggedInException(String cause){
		super("User '" + cause + "'is not logged in!");
	}

}
