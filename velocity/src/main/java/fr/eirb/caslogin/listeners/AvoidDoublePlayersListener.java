package fr.eirb.caslogin.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import fr.eirb.caslogin.utils.ProxyUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class AvoidDoublePlayersListener {
	@Subscribe
	public void onPreLogin(PreLoginEvent ev) {
		if (ProxyUtils.usernameExists(ev.getUsername()))
			ev.setResult(PreLoginEvent.PreLoginComponentResult.denied(MiniMessage
					.miniMessage()
					.deserialize("<red>Ce joueur est déjà connecté, veuillez changer de compte Minecraft!"))
			);
	}
}
