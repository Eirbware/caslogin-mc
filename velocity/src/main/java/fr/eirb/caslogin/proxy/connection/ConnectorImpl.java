package fr.eirb.caslogin.proxy.connection;

import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.util.GameProfile;
import fr.eirb.caslogin.configuration.ConfigurationManager;
import fr.eirb.caslogin.utils.GameProfileUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

class ConnectorImpl implements Connector {
	private final Player player;
	private RegisteredServer server;
	private GameProfile fakeIdentity;

	public ConnectorImpl(Player p) {
		this.player = p;
	}

	@Override
	public Connector to(RegisteredServer server) {
		this.server = server;
		return this;
	}

	@Override
	public Connector as(GameProfile newProfile) {
		this.fakeIdentity = newProfile;
		return this;
	}

	@Override
	public CompletableFuture<ConnectionRequestBuilder.Result> connect() {
		if(server == null)
			return CompletableFuture.failedFuture(new IllegalArgumentException("No server specified"));
		GameProfile oldGameProfile = GameProfileUtils.cloneGameProfile(player.getGameProfile());
		if (fakeIdentity != null) {
			GameProfileUtils.setToGameProfile(player.getGameProfile(), fakeIdentity);
		}
		return player.createConnectionRequest(server)
				.connect()
				.handle((result, throwable) -> {
					if(throwable != null){
						player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.user_disconnected_no_reason")));
						return new ConnectionRequestBuilder.Result() {
							@Override
							public ConnectionRequestBuilder.Status getStatus() {
								return result == null ? ConnectionRequestBuilder.Status.CONNECTION_CANCELLED : result.getStatus();
							}

							@Override
							public Optional<Component> getReasonComponent() {
								return result == null ? Optional.empty() : result.getReasonComponent();
							}

							@Override
							public RegisteredServer getAttemptedConnection() {
								return server;
							}
						};
					}
					return result;
				});
	}
}
