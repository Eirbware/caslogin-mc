package fr.eirb.caslogin.exceptions.login;

public class AlreadyBannedException extends Exception {
	public AlreadyBannedException(String user){
		super("The user '" + user + "' is already banned.");
	}
}
