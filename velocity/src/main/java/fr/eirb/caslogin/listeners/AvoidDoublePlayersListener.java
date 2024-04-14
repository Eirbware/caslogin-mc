package fr.eirb.caslogin.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.util.GameProfile;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.utils.PlayerUtils;
import fr.eirb.caslogin.utils.ProxyUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class AvoidDoublePlayersListener {
	@Subscribe
	public void onPreLogin(PreLoginEvent ev) {
		if (ProxyUtils.usernameExists(ev.getUsername()))
			ev.setResult(PreLoginEvent.PreLoginComponentResult.denied(MiniMessage
					.miniMessage()
					.deserialize("<red>Ce joueur est déjà connecté, veuillez changer de compte Minecraft !"))
			);
	}

	@Subscribe
	public void onDisconnect(DisconnectEvent ev){
		GameProfile trueIdentity = PlayerUtils.getTrueIdentity(ev.getPlayer());
		if(trueIdentity == null)
			return;
		if(ProxyUtils.usernameExists(trueIdentity.getName())){
			ProxyUtils.clearUsername(trueIdentity.getName());
		}
	}
}
