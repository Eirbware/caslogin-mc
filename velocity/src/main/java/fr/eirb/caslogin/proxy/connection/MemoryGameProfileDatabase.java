package fr.eirb.caslogin.proxy.connection;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.GameProfile;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MemoryGameProfileDatabase implements GameProfileDatabase{
	private final Map<Player, GameProfile> profileMap = new HashMap<>();

	@Override
	public CompletableFuture<Void> put(Player key, GameProfile value) {
		profileMap.put(key, value);
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<Void> remove(Player key) {
		if(!profileMap.containsKey(key))
			return CompletableFuture.failedFuture(new NoSuchElementException());
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<Optional<GameProfile>> get(Player key) {
		return CompletableFuture.completedFuture(Optional.ofNullable(profileMap.get(key)));
	}

	@Override
	public CompletableFuture<Boolean> contains(Player key) {
		return CompletableFuture.completedFuture(profileMap.containsKey(key));
	}

	@Override
	public CompletableFuture<Boolean> containsValue(GameProfile value) {
		for(var gameprofile : profileMap.values()){
			if(gameprofile.equals(value))
				return CompletableFuture.completedFuture(true);
		}
		return CompletableFuture.completedFuture(false);
	}
}
