package fr.eirb.caslogin.role.impl;

import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.model.LoggedUser;
import fr.eirb.caslogin.role.RoleManager;
import fr.eirb.caslogin.utils.PlayerUtils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class LuckPermsRoleManager implements RoleManager {
	private final LuckPerms api;

	public LuckPermsRoleManager(LuckPerms api) {
		this.api = api;
	}

	// THIS IS BLOCKING! SHOULD NOT BE USED OUTSIDE ALREADY ASYNC CALLBACK
	private List<Group> getRolesAsGroupsOfUser(LoggedUser loggedUser) {
		return Arrays.stream(loggedUser.user().roles())
				.map(role -> role.getGroup(api))
				.map(CompletableFuture::join)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList();
	}

	private Consumer<User> loadRolesOfUser(LoggedUser loggedUser) {
		return (user) -> {
			if (user == null)
				return;
			// Safe to do because we're already in async :D
			List<Group> groups = getRolesAsGroupsOfUser(loggedUser);
			for (Group group : groups) {
				user.data().add(InheritanceNode.builder(group).build());
			}
			api.getUserManager().saveUser(user);
		};
	}

	private Consumer<User> removeRolesOfUser(LoggedUser loggedUser) {
		return (user) -> {
			if (user == null)
				return;
			// Safe to do because we're already in async :D
			List<Group> groups = getRolesAsGroupsOfUser(loggedUser);
			for (Group group : groups) {
				user.data().remove(InheritanceNode.builder(group).build());
			}
			api.getUserManager().saveUser(user);
		};
	}

	@Override
	public void updateUserRoles(LoggedUser loggedUser) {
		var consumerFunc = loadRolesOfUser(loggedUser);
		api.getUserManager().loadUser(loggedUser.getFakeUserUUID())
				.thenAcceptAsync(consumerFunc);
		api.getUserManager().loadUser(loggedUser.getUuid())
				.thenAcceptAsync(consumerFunc);
	}

	@Override
	public void removeUserRoles(LoggedUser loggedUser) {
		var consumer = removeRolesOfUser(loggedUser);
		api.getUserManager().loadUser(loggedUser.getFakeUserUUID())
				.thenAcceptAsync(consumer);
		api.getUserManager().loadUser(loggedUser.getUuid())
				.thenAcceptAsync(consumer);
	}

	@Override
	public void removePlayerData(Player player) {
		api.getUserManager().loadUser(PlayerUtils.getTrueIdentity(player).getId())
				.thenAccept(user -> {
					user.data().clear();
					api.getUserManager().saveUser(user);
				});
	}
}
