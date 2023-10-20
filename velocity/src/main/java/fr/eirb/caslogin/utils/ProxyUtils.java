package fr.eirb.caslogin.utils;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ProxyUtils {

	private static final Class<?> velocityClass;
	private static final Class<?> connectedPlayerClass;
	private static final Method unregisterConnectionMethod;

	public static void unregisterConnection(ProxyServer server, Player player) {
		unregisterConnectionMethod.setAccessible(true);
		try {
			unregisterConnectionMethod.invoke(server, connectedPlayerClass.cast(player));
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}finally {
			unregisterConnectionMethod.setAccessible(false);
		}
	}

	static {

		try {
			velocityClass = Class.forName("com.velocitypowered.proxy.VelocityServer");
			connectedPlayerClass = Class.forName("com.velocitypowered.proxy.connection.client.ConnectedPlayer");
			unregisterConnectionMethod = velocityClass.getDeclaredMethod("unregisterConnection", connectedPlayerClass);
		} catch (ClassNotFoundException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
}
