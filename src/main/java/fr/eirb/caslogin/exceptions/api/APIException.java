package fr.eirb.caslogin.exceptions.api;

public class APIException extends Exception{
	public final Errors error;
	public APIException(Errors error){
		this.error = error;
	}
}
