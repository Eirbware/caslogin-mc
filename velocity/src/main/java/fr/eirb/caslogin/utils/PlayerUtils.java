package fr.eirb.caslogin.utils;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.crypto.IdentifiedKey;
import fr.eirb.caslogin.manager.ConfigurationManager;
import io.netty.channel.Channel;

import java.lang.reflect.Field;

public class PlayerUtils {
	public static boolean isPlayerInLimbo(Player player){
		if(player.getCurrentServer().isEmpty())
			return true;
		return player.getCurrentServer().get().getServerInfo().getName().equals(ConfigurationManager.getLimboServerName());
	}

}
