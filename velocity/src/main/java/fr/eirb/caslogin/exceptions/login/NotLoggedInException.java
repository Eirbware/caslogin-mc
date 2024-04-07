package fr.eirb.caslogin.exceptions.login;


import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.model.LoggedUser;

public class NotLoggedInException extends LoginException{

	public NotLoggedInException(Player cause){
		super("Player '" + cause.getUsername() + "'is not logged in!");
	}

	public NotLoggedInException(LoggedUser cause){
		super("User '" + cause.user().login() + "'is not logged in!");
	}

}
