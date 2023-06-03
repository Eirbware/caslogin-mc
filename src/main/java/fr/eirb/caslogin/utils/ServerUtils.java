package fr.eirb.caslogin.utils;

import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class ServerUtils {
	public static void removePlayerFromPlayerList(Server server, Player player){
		CraftPlayer nmsPlayer = (CraftPlayer) player;
		CraftServer nmsServer = (CraftServer) server;
		nmsServer.getHandle().players.remove(nmsPlayer.getHandle());
	}

	public static void addPlayerToPlayerList(Server server, Player player){
		CraftServer nmsServer = (CraftServer) server;
		CraftPlayer nmsPlayer = (CraftPlayer) player;
		nmsServer.getHandle().players.add(nmsPlayer.getHandle());
	}
}
