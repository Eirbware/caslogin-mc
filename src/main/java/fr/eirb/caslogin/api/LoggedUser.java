package fr.eirb.caslogin.api;

public class LoggedUser {
	private CasUser user;
	private String uuid;

	public CasUser getUser(){
		return user;
	}

	public String getUuid(){
		return uuid;
	}
}
