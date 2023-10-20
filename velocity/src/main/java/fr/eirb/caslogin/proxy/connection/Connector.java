package fr.eirb.caslogin.proxy.connection;

import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.util.GameProfile;
import fr.eirb.caslogin.api.model.LoggedUser;

import java.util.concurrent.CompletableFuture;

public interface Connector {
	Connector to(RegisteredServer server);
	Connector as(LoggedUser newProfile);
	CompletableFuture<ConnectionRequestBuilder.Result> connect();

	public static Connector get(Player p){
		return new ConnectorImpl(p);
	}
}