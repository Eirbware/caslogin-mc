package fr.eirb.caslogin.api.model;

import com.google.common.base.Objects;
import com.velocitypowered.api.util.UuidUtils;

import java.util.UUID;

public class LoggedUser {
	private CasUser user;
	private String uuid;

	public CasUser getUser(){
		return user;
	}

	public UUID getUuid(){
		return UUID.fromString(uuid);
	}

	public UUID getFakeUserUUID(){
		return UuidUtils.generateOfflinePlayerUuid(getUser().getLogin());
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