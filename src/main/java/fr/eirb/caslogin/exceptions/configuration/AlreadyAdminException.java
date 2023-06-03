package fr.eirb.caslogin.exceptions.configuration;

public class AlreadyAdminException extends Exception {
	public AlreadyAdminException(String login) {
		super("The user '" + login + "' is already an administrator.");
	}
}
