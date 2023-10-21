package fr.eirb.caslogin.utils;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.eirb.caslogin.api.model.LoggedUser;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public final class ProxyUtils {
	private static Class<?> velocityClass = null;
	private static Class<?> connectedPlayerClass = null;
	private static Method unregisterConnectionMethod = null;
	private static Field connectionsByNameField = null;
	private static Field connectionsByUuidField = null;

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

	public static void unregisterConnection(ProxyServer server, Player player) {
		initVelocityClass(server);
		initConnectedPlayerClass(player);
		initUnregisterConnectionMethod();

		unregisterConnectionMethod.setAccessible(true);
		try {
			unregisterConnectionMethod.invoke(server, connectedPlayerClass.cast(player));
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		} finally {
			unregisterConnectionMethod.setAccessible(false);
		}
	}

	private static void initUnregisterConnectionMethod() {
		if (unregisterConnectionMethod == null && velocityClass != null && connectedPlayerClass != null) {
			try {
				unregisterConnectionMethod = velocityClass.getDeclaredMethod("unregisterConnection", connectedPlayerClass);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void addLoggedUserToProxy(ProxyServer server, Player player, LoggedUser user) {
		initVelocityClass(server);
		initConnectedPlayerClass(player);
		initConnectionsFields();

		Object connectedPlayer = connectedPlayerClass.cast(player);
		getConnectionsByUuid(server).put(user.getFakeUserUUID(), connectedPlayer);
		getConnectionsByName(server).put(user.getUser().getLogin(), connectedPlayer);
	}

	public static void removeLoggedUserFromProxy(ProxyServer server, Player player, LoggedUser user) {
		initVelocityClass(server);
		initConnectedPlayerClass(player);
		initConnectionsFields();

		Object connectedPlayer = connectedPlayerClass.cast(player);
		getConnectionsByUuid(server).remove(user.getFakeUserUUID(), connectedPlayer);
		getConnectionsByName(server).remove(user.getUser().getLogin(), connectedPlayer);
	}
}
