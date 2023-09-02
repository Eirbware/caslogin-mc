package fr.eirb.caslogin.exceptions.login;

public class CouldNotGenerateCSRFTokenException extends LoginException{
	public CouldNotGenerateCSRFTokenException() {
		super("Could not generate a CSRF Token.");
	}
}
