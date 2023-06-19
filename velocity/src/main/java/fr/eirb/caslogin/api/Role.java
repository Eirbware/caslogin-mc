package fr.eirb.caslogin.api;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public enum Role {
	ADMINISTRATOR,
	MODERATOR;

	public CompletableFuture<Optional<Group>> getGroup(LuckPerms api){
		return api.getGroupManager().loadGroup(this.name().toLowerCase());
	}
}
