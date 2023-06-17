package fr.eirb.caslogin.api;

import com.google.common.base.Objects;

public class LoggedUser {
	private CasUser user;
	private String uuid;

	public CasUser getUser(){
		return user;
	}

	public String getUuid(){
		return uuid;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LoggedUser that = (LoggedUser) o;
		return Objects.equal(user, that.user) && Objects.equal(uuid, that.uuid);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(user, uuid);
	}
}
