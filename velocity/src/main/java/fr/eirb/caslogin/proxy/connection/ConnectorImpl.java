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
		GameProfile oldGameProfile = GameProfileUtils.cloneGameProfile(player.getGameProfile());
		if (fakeIdentity != null) {
			GameProfileUtils.setToGameProfile(player.getGameProfile(), fakeIdentity);
		}
		ConnectionRequestBuilder req = player.createConnectionRequest(server);
		return req
				.connect()
				.whenComplete((result, throwable) -> {
					GameProfileUtils.setToGameProfile(player.getGameProfile(), oldGameProfile);
					if(throwable != null){
						player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.user_disconnected_no_reason")));
						return;
					}
					player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.login.success")));
				});
	}
}
