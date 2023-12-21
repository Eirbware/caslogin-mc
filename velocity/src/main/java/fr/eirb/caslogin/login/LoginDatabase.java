package fr.eirb.caslogin.login;

import fr.eirb.caslogin.AsyncBiMap;
import fr.eirb.caslogin.model.LoggedUser;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface LoginDatabase extends AsyncBiMap<UUID, LoggedUser> {
	CompletableFuture<Void> logoutUserByLogin(String login);
	CompletableFuture<Optional<UUID>> getUUIDFromUserByLogin(String login);
}
