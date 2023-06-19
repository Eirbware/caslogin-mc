package fr.eirb.caslogin.manager.impl;

import com.velocitypowered.api.proxy.ProxyServer;
import fr.eirb.caslogin.api.LoggedUser;
import fr.eirb.caslogin.manager.RoleManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LuckPermsRoleManager implements RoleManager {
	private final LuckPerms api;
	private final ProxyServer proxy;

	public LuckPermsRoleManager(LuckPerms api, ProxyServer proxy) {
		this.api = api;
		this.proxy = proxy;
	}

	// THIS IS BLOCKING! SHOULD NOT BE USED OUTSIDE OF ALREADY ASYNC CALLBACK
	private List<Group> getRolesAsGroupsOfUser(LoggedUser loggedUser){
		return Arrays.stream(loggedUser.getUser().getRoles())
				.map(role -> role.getGroup(api))
				.map(CompletableFuture::join)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList();
	}

	@Override
	public void updateUserRoles(LoggedUser loggedUser) {
		api.getUserManager().loadUser(loggedUser.getFakeUserUUID())
				.thenAcceptAsync(user -> {
					if (user == null)
						return;
					// Safe to do because we're already in async :D
					List<Group> groups = getRolesAsGroupsOfUser(loggedUser);


					user.getInheritedGroups(QueryOptions.builder(QueryMode.CONTEXTUAL).build()).addAll(groups);
				});
	}

	@Override
	public void removeUserRoles(LoggedUser loggedUser) {
		api.getUserManager().loadUser(loggedUser.getFakeUserUUID())
				.thenAcceptAsync(user -> {
					if (user == null)
						return;
					// Safe to do because we're already in async :D
					List<Group> groups = getRolesAsGroupsOfUser(loggedUser);

					user.getInheritedGroups(QueryOptions.builder(QueryMode.CONTEXTUAL).build()).removeAll(groups);
				});
	}
}
