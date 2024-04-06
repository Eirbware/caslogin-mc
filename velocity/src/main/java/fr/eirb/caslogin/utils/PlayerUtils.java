package fr.eirb.caslogin.utils;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.GameProfile;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.model.LoggedUser;
import fr.eirb.caslogin.configuration.ConfigurationManager;
import fr.eirb.caslogin.events.LoginEvent;
import fr.eirb.caslogin.proxy.connection.Connector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.HashMap;
import java.util.function.Consumer;

public final class PlayerUtils {
	private static final HashMap<Player, GameProfile> playerToProfileMap = new HashMap<>();

	@Subscribe(order = PostOrder.FIRST)
	public void onJoin(PostLoginEvent ev) {
		playerToProfileMap.put(ev.getPlayer(), GameProfileUtils.cloneGameProfile(ev.getPlayer().getGameProfile()));
	}

	@Subscribe(order = PostOrder.LAST)
	public void onDisconnect(DisconnectEvent ev) {
		Player p = ev.getPlayer();
//		restoreGameProfile(p);
//		ProxyUtils.unregisterConnection(CasLogin.get().getProxy(), p);
		playerToProfileMap.remove(p);
	}

	public static Consumer<LoggedUser> logPlayer(Player player) {
		return (loggedUser) -> CasLogin.get().getProxy().getEventManager()
				.fire(new LoginEvent(player, loggedUser))
				.thenAccept(loginEvent -> {
					Component message = MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.login.success"));
					player.sendMessage(message);
					Connector.get(player).to(loginEvent.server()).as(loggedUser)
							.connect()
							.whenComplete((result, throwable) -> {
								if (throwable != null || !result.isSuccessful()) {
									Component disconnectMessage = result.getReasonComponent().isEmpty()
											? MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.user_disconnected_no_reason"))
											: MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.user_disconnected"));
									if (result.getReasonComponent().isPresent())
										disconnectMessage = disconnectMessage.append(result.getReasonComponent().get());
									player.sendMessage(disconnectMessage);
								}
							});
				});
	}

	public static GameProfile getTrueIdentity(Player player) {
		return playerToProfileMap.get(player);
	}

	public static void restoreGameProfile(Player player) {
		GameProfileUtils.setToGameProfile(player.getGameProfile(), playerToProfileMap.get(player));
	}

	public static boolean isPlayerInLimbo(Player player) {
		if (player.getCurrentServer().isEmpty())
			return true;
		return player.getCurrentServer().get().getServerInfo().equals(CasLogin.getEntrypointServer().getServerInfo());
	}

}
