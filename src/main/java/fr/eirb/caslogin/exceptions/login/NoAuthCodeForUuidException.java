package fr.eirb.caslogin.exceptions.login;

public class NoAuthCodeForUuidException extends LoginException{
	public NoAuthCodeForUuidException() {
		super("There was no auth code for the given uuid.");
	}
}
