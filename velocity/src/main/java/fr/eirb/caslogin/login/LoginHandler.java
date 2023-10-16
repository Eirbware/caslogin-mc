package fr.eirb.caslogin.login;

import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.api.model.LoggedUser;
import fr.eirb.caslogin.exceptions.login.NotLoggedInException;

import java.util.concurrent.CompletableFuture;

public interface LoginHandler {
	CompletableFuture<LoggedUser> login(Player player);
	CompletableFuture<Void> logout(Player player) throws NotLoggedInException;
}
