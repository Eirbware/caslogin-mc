package fr.eirb.caslogin.exceptions.configuration;

public class NotAdminException extends Exception {
	public NotAdminException(String login){
		super("The user '" + login + "' is not an administrator.");
	}
}
