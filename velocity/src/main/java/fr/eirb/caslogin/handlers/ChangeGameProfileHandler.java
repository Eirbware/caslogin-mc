package fr.eirb.caslogin.handlers;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.GameProfile;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.manager.LoginManager;
import fr.eirb.caslogin.utils.GameProfileUtils;

import java.util.HashMap;
import java.util.Map;

public class ChangeGameProfileHandler {

	private static ChangeGameProfileHandler INSTANCE;

	private final Map<Player, GameProfile> oldProfiles = new HashMap<>();

	public static ChangeGameProfileHandler getINSTANCE() {
		return INSTANCE;
	}
	public ChangeGameProfileHandler(){
		INSTANCE = this;
	}

	@Subscribe(order = PostOrder.LAST)
	public void changeGameProfile(ServerPreConnectEvent ev){
		if(ev.getResult().getServer().get().getServerInfo().equals(CasLogin.getEntrypointServer().getServerInfo())) {
			restoreGameProfile(ev.getPlayer());
			return;
		}

		Player player = ev.getPlayer();
		LoginManager.getLoggedPlayer(player).ifPresent((loggedUser -> {
			CasLogin.getINSTANCE().getLogger().info(String.format("Changing game profile of player '%s'", ev.getPlayer().getUsername()));
			GameProfile prof = player.getGameProfile();
			GameProfile oldProf = GameProfileUtils.cloneGameProfile(prof);
			oldProfiles.put(player, oldProf);
			GameProfileUtils.setName(prof, loggedUser.getUser().getLogin());
			GameProfileUtils.setUUID(prof, loggedUser.getFakeUserUUID());
		}));
	}

	@Subscribe
	public void restoreGameProfileOnServerChange(ServerConnectedEvent ev){
		restoreGameProfile(ev.getPlayer());
	}

	@Subscribe
	public void restoreGameProfileOnKick(KickedFromServerEvent ev){
		System.out.println("KICKED");
		restoreGameProfile(ev.getPlayer());
	}

	public void restoreGameProfile(Player player){
		if(!oldProfiles.containsKey(player)) {
			return;
		}
		GameProfile prof = player.getGameProfile();
		GameProfile oldProf = oldProfiles.get(player);
		CasLogin.getINSTANCE().getLogger().info(String.format("Restoring profile of player '%s'", oldProf.getName()));
		oldProfiles.remove(player);
		GameProfileUtils.setToGameProfile(prof, oldProf);
	}
}
