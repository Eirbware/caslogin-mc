package fr.eirb.caslogin.utils;

import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.CasLogin;

public class PlayerUtils {
	public static boolean isPlayerInLimbo(Player player){
		if(player.getCurrentServer().isEmpty())
			return true;
		return player.getCurrentServer().get().getServerInfo().equals(CasLogin.getEntrypointServer().getServerInfo());
	}

}
