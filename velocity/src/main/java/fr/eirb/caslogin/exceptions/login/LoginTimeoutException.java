package fr.eirb.caslogin.exceptions.login;

public class LoginTimeoutException extends LoginException{
	public LoginTimeoutException(int timeoutSeconds) {
		super(String.format("Login attempt timed out after %d seconds", timeoutSeconds));
	}
}
