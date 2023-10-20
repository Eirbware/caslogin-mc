package fr.eirb.caslogin.utils;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ProxyUtils {
	private static Class<?> velocityClass = null;
	private static Class<?> connectedPlayerClass = null;
	private static Method unregisterConnectionMethod = null;

	public static void unregisterConnection(ProxyServer server, Player player) {
		if (velocityClass == null)
			velocityClass = server.getClass();

		if (connectedPlayerClass == null)
			connectedPlayerClass = player.getClass();

		if (unregisterConnectionMethod == null) {
			try {
				unregisterConnectionMethod = velocityClass.getDeclaredMethod("unregisterConnection", connectedPlayerClass);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}

		unregisterConnectionMethod.setAccessible(true);
		try {
			unregisterConnectionMethod.invoke(server, connectedPlayerClass.cast(player));
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		} finally {
			unregisterConnectionMethod.setAccessible(false);
		}
	}
}
