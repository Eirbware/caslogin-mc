package fr.eirb.caslogin.utils;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

public final class ProxyUtils {
	private static Class<?> velocityClass = null;
	private static Class<?> connectedPlayerClass = null;
	private static Method unregisterConnectionMethod = null;

	private static void initVelocityClass(ProxyServer server){
		if (velocityClass == null)
			velocityClass = server.getClass();
	}

	private static void initConnectedPlayerClass(Player player){
		if (connectedPlayerClass == null)
			connectedPlayerClass = player.getClass();
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
}
