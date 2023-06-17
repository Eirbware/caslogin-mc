package fr.eirb.caslogin.exceptions.login;

public class LoginAlreadyTakenException extends LoginException{

	public LoginAlreadyTakenException() {
		super("The login was already taken!");
	}
}
