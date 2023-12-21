package fr.eirb.caslogin.proxy.connection;

import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.model.LoggedUser;
import fr.eirb.caslogin.configuration.ConfigurationManager;
import fr.eirb.caslogin.events.LoginEvent;
import fr.eirb.caslogin.utils.GameProfileUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

class ConnectorImpl implements Connector {
	private final Player player;
	private RegisteredServer server;
	private LoggedUser identity;

	public ConnectorImpl(Player p) {
		this.player = p;
	}

	@Override
	public Connector to(@NotNull RegisteredServer server) {
		this.server = server;
		return this;
	}

	@Override
	public Connector as(LoggedUser newProfile) {
		this.identity = newProfile;
		return this;
	}

	@Override
	public CompletableFuture<ConnectionRequestBuilder.Result> connect() {
		if (server == null)
			return CompletableFuture.failedFuture(new IllegalArgumentException("No server specified"));
		if (identity != null) {
			GameProfileUtils.setToGameProfile(player.getGameProfile(), identity.getFakeGameProfile());
		}
		return player.createConnectionRequest(server)
				.connect()
				.handle((result, throwable) -> {
					if (throwable != null) {
						if (result != null && result.getReasonComponent().isPresent())
							player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.user_disconnected"))
									.append(result.getReasonComponent().get()));
						else if (result != null && result.getReasonComponent().isEmpty()) {
							player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.user_disconnected_no_reason")));
						}
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
				})
				.whenComplete((result, throwable) -> {
					if (throwable != null || !result.isSuccessful())
						return;
					if (this.identity != null)
						CasLogin.get().getProxy().getEventManager().fire(new LoginEvent(player, this.identity));
				});
	}
}
