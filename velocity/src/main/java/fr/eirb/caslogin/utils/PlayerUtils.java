package fr.eirb.caslogin.utils;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.GameProfile;
import fr.eirb.caslogin.CasLogin;
import io.netty.handler.proxy.ProxyConnectionEvent;

import java.util.HashMap;

public final class PlayerUtils {
	private static final HashMap<Player, GameProfile> playerToProfileMap = new HashMap<>();

	@Subscribe
	public void onJoin(PostLoginEvent ev) {
		playerToProfileMap.put(ev.getPlayer(), GameProfileUtils.cloneGameProfile(ev.getPlayer().getGameProfile()));
	}

	@Subscribe
	public void onDisconnect(DisconnectEvent ev) {
		Player p = ev.getPlayer();
		restoreGameProfile(p);
		ProxyUtils.unregisterConnection(CasLogin.get().getProxy(), p);
		playerToProfileMap.remove(p);
	}

	public static GameProfile getTrueIdentity(Player player) {
		return playerToProfileMap.get(player);
	}

	private void restoreGameProfile(Player player) {
		GameProfileUtils.setToGameProfile(player.getGameProfile(), playerToProfileMap.get(player));
	}

	public static boolean isPlayerInLimbo(Player player) {
		if (player.getCurrentServer().isEmpty())
			return true;
		return player.getCurrentServer().get().getServerInfo().equals(CasLogin.getEntrypointServer().getServerInfo());
	}

}
