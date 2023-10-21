package fr.eirb.caslogin.login;

import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.api.model.LoggedUser;
import fr.eirb.caslogin.exceptions.login.NotLoggedInException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface LoginHandler {
	CompletableFuture<List<LoggedUser>> getLoggedUsers();

	/**
	 * Will log in the player and return the LoggedUser or not. It can fail
	 * @param player The minecraft player to log in
	 * @return A completable future that will either fail, or returns the LoggedUser
	 */
	CompletableFuture<LoggedUser> login(Player player);
	CompletableFuture<Void> logout(Player player) throws NotLoggedInException;
}
