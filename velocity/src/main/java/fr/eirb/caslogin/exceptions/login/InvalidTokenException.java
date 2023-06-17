package fr.eirb.caslogin.exceptions.login;

public class InvalidTokenException extends LoginException{
	public InvalidTokenException() {
		super("Invalid token.");
	}
}
