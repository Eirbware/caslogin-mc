package fr.eirb.caslogin.utils;

import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.manager.ConfigurationManager;

public class PlayerUtils {
	public static boolean isPlayerInLimbo(Player player){
		if(player.getCurrentServer().isEmpty())
			return true;
		return player.getCurrentServer().get().getServerInfo().getName().equals(ConfigurationManager.getLimboServerName());
	}

}
