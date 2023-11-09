package fr.eirb.caslogin.exceptions.login;

import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.api.model.LoggedUser;

public class UserBannedException extends LoginException{
	public UserBannedException(Player player, LoggedUser cause) {
		super(String.format("The player '%s' tried to connect to '%s' which is banned", player.getUsername(), cause.getUser().getLogin()));
	}
}
