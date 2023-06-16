package fr.eirb.caslogin.api;

public class CasUser {
	private String login;
	private String ecole;
	private Role[] roles;
	public String getLogin(){
		return login;
	}

	public String getEcole() {
		return ecole;
	}

	public Role[] getRoles() {
		return roles;
	}
}
