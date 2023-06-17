package fr.eirb.caslogin.exceptions.login;

public class NotBannedException extends Exception{
	public NotBannedException(String user){
		super("The user '" + user + "' is not banned.");
	}
}
