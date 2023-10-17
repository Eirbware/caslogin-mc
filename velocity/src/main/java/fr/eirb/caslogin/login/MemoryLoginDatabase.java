package fr.eirb.caslogin.login;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.api.model.LoggedUser;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MemoryLoginDatabase implements LoginDatabase {
	private final BiMap<Player, @NotNull LoggedUser> loggedUserMap = HashBiMap.create();

	@Override
	public CompletableFuture<Optional<Player>> getByValue(LoggedUser value) {
		return CompletableFuture.completedFuture(Optional.ofNullable(loggedUserMap.inverse().get(value)));
	}

	@Override
	public CompletableFuture<Void> put(Player key, LoggedUser value) {
		loggedUserMap.put(key, value);
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<Void> remove(Player key) {
		if (!loggedUserMap.containsKey(key))
			return CompletableFuture.failedFuture(new NoSuchElementException());
		loggedUserMap.remove(key);
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<Optional<LoggedUser>> get(Player key) {
		return CompletableFuture.completedFuture(Optional.ofNullable(loggedUserMap.get(key)));
	}

	@Override
	public CompletableFuture<Boolean> contains(Player key) {
		return CompletableFuture.completedFuture(loggedUserMap.containsKey(key));
	}

	@Override
	public CompletableFuture<Boolean> containsValue(LoggedUser value) {
		return CompletableFuture.completedFuture(loggedUserMap.containsValue(value));
	}

	@Override
	public CompletableFuture<Void> logoutUserByLogin(String login) {
		Optional<LoggedUser> user = loggedUserMap.values().stream().filter(loggedUser -> loggedUser.getUser().getLogin().equals(login)).findFirst();
		if(user.isEmpty())
			return CompletableFuture.failedFuture(new NoSuchElementException());
		loggedUserMap.inverse().remove(user.get());
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<Optional<Player>> getPlayerFromUserByLogin(String login) {
		Optional<LoggedUser> user = loggedUserMap.values().stream().filter(loggedUser -> loggedUser.getUser().getLogin().equals(login)).findFirst();
		if(user.isEmpty())
			return CompletableFuture.failedFuture(new NoSuchElementException());
		return CompletableFuture.completedFuture(Optional.ofNullable(loggedUserMap.inverse().get(user.get())));
	}
}
