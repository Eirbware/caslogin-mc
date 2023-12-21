package fr.eirb.caslogin.events;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.model.LoggedUser;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired whenever a player logs in on the entrypointServer.
 * Whenever it is fired, it should wait for all listeners to complete, and the listeners can change the
 * destination server, which by default is loggedEntrypointServer
 */
public class LoginEvent {
	private final LoggedUser loggedUser;
	private final Player player;
	private @NotNull  RegisteredServer server;

	public LoginEvent(Player player, LoggedUser loggedUser){
		this.player = player;
		this.loggedUser = loggedUser;
		this.server = CasLogin.getLoggedEntrypointServer();
	}

	public LoggedUser loggedUser() {
		return loggedUser;
	}

	public Player player() {
		return player;
	}

	public @NotNull RegisteredServer server() {
		return server;
	}

	public void setServer(@NotNull  RegisteredServer server) {
		this.server = server;
	}
}
