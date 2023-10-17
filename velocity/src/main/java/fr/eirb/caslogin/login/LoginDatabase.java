package fr.eirb.caslogin.login;

import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.AsyncBiMap;
import fr.eirb.caslogin.api.model.LoggedUser;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface LoginDatabase extends AsyncBiMap<Player, LoggedUser> {
	CompletableFuture<Void> logoutUserByLogin(String login);
	CompletableFuture<Optional<Player>> getPlayerFromUserByLogin(String login);
}
