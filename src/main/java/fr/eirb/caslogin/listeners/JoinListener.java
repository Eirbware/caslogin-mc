package fr.eirb.caslogin.listeners;

import com.destroystokyo.paper.profile.PlayerProfile;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.manager.ConfigurationManager;
import fr.eirb.caslogin.manager.LoginManager;
import fr.eirb.caslogin.utils.MessagesEnum;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class JoinListener implements Listener {

	@EventHandler
	public void preLoginEvent(AsyncPlayerPreLoginEvent ev) {
		PlayerProfile profile = ev.getPlayerProfile();
		if (CasLogin.isNotLoggedIn(profile.getId()))
			return;
		profile.setName(LoginManager.INSTANCE.getLogin(profile.getId()));
		ev.setPlayerProfile(profile);
	}

	@EventHandler()
	public void onServerJoin(PlayerJoinEvent ev) {
		ev.getPlayer().setOp(false);
		CasLogin instance = CasLogin.INSTANCE;
		UUID playerUUID = ev.getPlayer().getUniqueId();
		if (CasLogin.isNotLoggedIn(playerUUID)) {
			ev.joinMessage(null);

			ev.getPlayer().setCollidable(false);
		} else {
			String playerLogin = LoginManager.INSTANCE.getLogin(playerUUID);
			if(LoginManager.INSTANCE.getBannedUsers().contains(playerLogin)) {
				ev.joinMessage(null);
				ev.getPlayer().kick(MiniMessage.miniMessage().deserialize(MessagesEnum.BANNED.str));
				return;
			}
			if (ConfigurationManager.INSTANCE.getAdmins().contains(playerLogin))
				instance.getServer().getPlayer(playerUUID).setOp(true);
			TranslatableComponent joinMessage = Component.translatable("multiplayer.player.joined")
					.args(Component.text(LoginManager.INSTANCE.getLogin(playerUUID)))
					.style(Style.style(NamedTextColor.YELLOW));
			ev.joinMessage(joinMessage);
		}

	}

	@EventHandler
	public void onServerQuit(PlayerQuitEvent ev) {
		if (CasLogin.isNotLoggedIn(ev.getPlayer())) {
			ev.quitMessage(null);
		}
	}
}
