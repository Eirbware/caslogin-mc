package fr.eirb.caslogin.exceptions.login;

public class LoginAlreadyTakenException extends LoginException{

	public LoginAlreadyTakenException(String login) {
		super("The login '" + login + "' was already taken!");
	}
}
