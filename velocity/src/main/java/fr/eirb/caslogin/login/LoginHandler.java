package fr.eirb.caslogin.login;

import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.api.model.LoggedUser;
import fr.eirb.caslogin.exceptions.login.AlreadyLoggedInException;
import fr.eirb.caslogin.exceptions.login.UserBannedException;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface LoginHandler {
	CompletableFuture<List<LoggedUser>> getLoggedUsers();

	/**
	 * Will log in the player and return the LoggedUser or not. It can fail
	 * @param player The minecraft player to log in
	 * @return A completable future that will either fail, or returns the LoggedUser
	 */
	CompletableFuture<LoggedUser> login(Player player);

	/**
	 *
	 * @param player
	 * @return A completable future that has the logged out player
	 */
	CompletableFuture<LoggedUser> logout(Player player);

	/**
	 *
	 * @param user
	 * @return A completable future that has the logged out player
	 */
	CompletableFuture<LoggedUser> logout(LoggedUser user);

	CompletableFuture<Void> ban(LoggedUser user);
	CompletableFuture<Void> tempBan(LoggedUser user, Duration duration);
}
