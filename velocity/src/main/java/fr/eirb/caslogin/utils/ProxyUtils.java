package fr.eirb.caslogin.utils;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.GameProfile;
import fr.eirb.caslogin.model.LoggedUser;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public final class ProxyUtils {
	private static Class<?> velocityClass = null;
	private static Class<?> connectedPlayerClass = null;
	private static Field connectionsByNameField = null;
	private static Field connectionsByUuidField = null;

	private static HashSet<String> shadowedPlayerNames = new HashSet<>();

	private static void initVelocityClass(ProxyServer server) {
		if (velocityClass == null)
			velocityClass = server.getClass();
	}

	private static void initConnectedPlayerClass(Player player) {
		if (connectedPlayerClass == null)
			connectedPlayerClass = player.getClass();
	}

	private static void initConnectionsFields() {
		if (velocityClass == null)
			return;
		try {
			connectionsByNameField = velocityClass.getDeclaredField("connectionsByName");
			connectionsByUuidField = velocityClass.getDeclaredField("connectionsByUuid");
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	private static Map<UUID, Object> getConnectionsByUuid(ProxyServer server) {
		connectionsByUuidField.setAccessible(true);
		Map<UUID, Object> ret = null;
		try {
			ret = (Map<UUID, Object>) connectionsByUuidField.get(server);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} finally {
			connectionsByUuidField.setAccessible(false);
		}
		return ret;
	}

	private static Map<String, Object> getConnectionsByName(ProxyServer server) {
		connectionsByNameField.setAccessible(true);
		Map<String, Object> ret = null;
		try {
			ret = (Map<String, Object>) connectionsByNameField.get(server);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} finally {
			connectionsByNameField.setAccessible(false);
		}
		return ret;
	}

	public static boolean usernameExists(String username){
		return shadowedPlayerNames.contains(username);
	}

	public static void addLoggedUserToProxy(ProxyServer server, Player player, LoggedUser user) {
		initVelocityClass(server);
		initConnectedPlayerClass(player);
		initConnectionsFields();

		Object connectedPlayer = connectedPlayerClass.cast(player);
		GameProfile trueIdentity = PlayerUtils.getTrueIdentity(player);
		var connectionsByUuid = getConnectionsByUuid(server);
		var connectionsByName = getConnectionsByName(server);

		connectionsByUuid.remove(trueIdentity.getId());
		connectionsByName.remove(trueIdentity.getName().toLowerCase());
		shadowedPlayerNames.add(trueIdentity.getName());
		connectionsByUuid.put(user.getFakeUserUUID(), connectedPlayer);
		connectionsByName.put(user.user().login(), connectedPlayer);
	}

	public static void removeLoggedUserFromProxy(ProxyServer server, Player player, LoggedUser user) {
		initVelocityClass(server);
		initConnectedPlayerClass(player);
		initConnectionsFields();

		Object connectedPlayer = connectedPlayerClass.cast(player);
		GameProfile trueIdentity = PlayerUtils.getTrueIdentity(player);
		var connectionsByUuid = getConnectionsByUuid(server);
		var connectionsByName = getConnectionsByName(server);

		connectionsByUuid.put(trueIdentity.getId(), connectedPlayer);
		connectionsByName.put(trueIdentity.getName().toLowerCase(), connectedPlayer);
		shadowedPlayerNames.remove(trueIdentity.getName());
		connectionsByUuid.remove(user.getFakeUserUUID());
		connectionsByName.remove(user.user().login());
	}

	public static void clearUsername(String name) {
		shadowedPlayerNames.remove(name);
	}
}
