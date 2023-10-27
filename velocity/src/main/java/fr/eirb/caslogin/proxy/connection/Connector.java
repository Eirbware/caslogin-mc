package fr.eirb.caslogin.proxy.connection;

import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fr.eirb.caslogin.api.model.LoggedUser;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface Connector {
	Connector to(@NotNull  RegisteredServer server);
	Connector as(LoggedUser newProfile);
	CompletableFuture<ConnectionRequestBuilder.Result> connect();

	static Connector get(Player p){
		return new ConnectorImpl(p);
	}
}
