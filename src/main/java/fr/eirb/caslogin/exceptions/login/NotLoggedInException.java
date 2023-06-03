package fr.eirb.caslogin.exceptions.login;

import org.bukkit.entity.Player;

public class NotLoggedInException extends LoginException{

	public NotLoggedInException(Player cause){
		super("Player '" + cause.getName() + "'is not logged in!");
	}

	public NotLoggedInException(String cause){
		super("User '" + cause + "'is not logged in!");
	}

}
