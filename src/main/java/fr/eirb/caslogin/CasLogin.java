package fr.eirb.caslogin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.*;
import fr.eirb.caslogin.commands.CasCommand;
import fr.eirb.caslogin.commands.CasTabCompleter;
import fr.eirb.caslogin.manager.LoginManager;
import fr.eirb.caslogin.utils.ServerUtils;
import fr.eirb.caslogin.listeners.FreezePlayer;
import fr.eirb.caslogin.listeners.JoinListener;
import okhttp3.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;

public final class CasLogin extends JavaPlugin {

	public static String PLUGIN_ID = "caslogin";
	public static CasLogin INSTANCE;
	private ProtocolManager protocolManager;


	@Override
	public void onEnable() {
		INSTANCE = this;
		// Plugin startup logic
		registerCommands();
		registerEvents();
		protocolManager = ProtocolLibrary.getProtocolManager();
		registerPacketListeners();

		OkHttpClient client = new OkHttpClient();
		Request req = new Request.Builder().url("https://publicobject.com/helloworld.txt").get().build();
		try(Response response = client.newCall(req).execute()){
			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
			Headers responseHeaders = response.headers();
			for (int i = 0; i < responseHeaders.size(); i++) {
				System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
			}

			assert response.body() != null;
			System.out.println(response.body().string());
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	private void registerCommands(){
		PluginCommand casCommand = getCommand("cas");
		assert casCommand != null;
		PluginCommand loginCommand = getCommand("login");
		assert  loginCommand != null;

		casCommand.setExecutor(new CasCommand());
		casCommand.setTabCompleter(new CasTabCompleter());

	}

	private void registerEvents(){
		getServer().getPluginManager().registerEvents(new JoinListener(), this);
		getServer().getPluginManager().registerEvents(new FreezePlayer(), this);
	}


	private void registerPacketListeners() {
		protocolManager.addPacketListener(new PacketAdapter(
				this,
				ListenerPriority.HIGHEST,
				PacketType.Play.Server.PLAYER_INFO
		) {
			@Override
			public void onPacketSending(PacketEvent event) {
				if (!event.getPacket().getPlayerInfoActions().read(0).contains(EnumWrappers.PlayerInfoAction.ADD_PLAYER))
					return;
				PlayerInfoData data = event.getPacket().getPlayerInfoDataLists().read(1).get(0);
				if (LoginManager.INSTANCE.isLoggedIn(data.getProfileId())) {
					return;
				}
				Player playerToRemove = getServer().getPlayer(data.getProfileId());
				ServerUtils.removePlayerFromPlayerList(getServer(), playerToRemove);
				event.setCancelled(true);
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.LOW, PacketType.Play.Server.WINDOW_ITEMS) {
			@Override
			public void onPacketSending(PacketEvent event) {
				if(isNotLoggedIn(event.getPlayer()))
					event.setCancelled(true);
			}
		});
	}

	public static boolean isNotLoggedIn(Player p){
		return !LoginManager.INSTANCE.isLoggedIn(p);
	}

	public static boolean isNotLoggedIn(UUID uuid){
		return !LoginManager.INSTANCE.isLoggedIn(uuid);
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}


}
