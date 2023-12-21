package fr.eirb.caslogin.model;

import com.google.common.base.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CasUser casUser = (CasUser) o;
		return Objects.equal(login, casUser.login) && Objects.equal(ecole, casUser.ecole) && Objects.equal(roles, casUser.roles);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(login, ecole, roles);
	}
}
