package fr.eirb.caslogin;

import com.google.common.base.Charsets;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.util.UuidUtils;
import fr.eirb.caslogin.api.LoggedUser;
import fr.eirb.caslogin.commands.CasCommand;
import fr.eirb.caslogin.events.PostLoginEvent;
import fr.eirb.caslogin.exceptions.login.NotLoggedInException;
import fr.eirb.caslogin.manager.ConfigurationManager;
import fr.eirb.caslogin.manager.LoginManager;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(
		id = Constants.PLUGIN_ID,
		name = Constants.PLUGIN_NAME,
		version = Constants.VERSION

)
public class CasLogin {

	private static final ChannelIdentifier CAS_FIX_CHANNEL = MinecraftChannelIdentifier.create("caslogin", "auth");

	@Inject
	private Logger logger;

	@Inject
	private ProxyServer proxy;

	@Inject
	public CasLogin(@DataDirectory Path pluginDir) {
		ConfigurationManager.loadConfig(pluginDir);
	}

	@Subscribe
	public void onProxyInit(ProxyInitializeEvent ev) {
		logger.info("Loading plugin...");
		registerCommands();
		logger.info("Plugin successfully loaded!");
	}

	@Subscribe
	public void onServerChange(ServerPostConnectEvent ev) {
		if(ev.getPlayer().getCurrentServer().isEmpty())
			return;
		RegisteredServer currentServer = ev.getPlayer().getCurrentServer().get().getServer();
		if (!currentServer.getServerInfo().getName().equals(ConfigurationManager.getLimboServerName())) {
			return;
		}
		logger.info("Logging out player " + ev.getPlayer().getUsername());
		try {
			LoginManager.logout(ev.getPlayer());
		} catch (NotLoggedInException ignored) {
		}

	}


	@Subscribe
	private void sendPluginMessageForFixes(PostLoginEvent ev) {
		Player player = ev.player();
		if(!LoginManager.loggedUserMap.containsKey(player.getUniqueId()))
			return;
		LoggedUser userForPlayer = LoginManager.loggedUserMap.get(player.getUniqueId());
		ServerConnection conn = player.getCurrentServer().orElseThrow();
		String message = player.getUniqueId() + ":" + UuidUtils.generateOfflinePlayerUuid(userForPlayer.getUser().getLogin());
		logger.info(String.format("Sending '%s' at '%s' to server '%s'", message, CAS_FIX_CHANNEL, conn.getServerInfo().getName()));
		conn.sendPluginMessage(CAS_FIX_CHANNEL, Charsets.UTF_8.encode(message).array());
	}

	@Subscribe
	public void onDisconnect(DisconnectEvent ev) {
		logger.info("Logging out player " + ev.getPlayer().getUsername());
		try {
			LoginManager.logout(ev.getPlayer());
		} catch (NotLoggedInException ignored) {
		}
	}

	private void registerCommands() {
		logger.info("Loading commands...");
		proxy.getCommandManager().register(CasCommand.createCasCommand(proxy));
		logger.info("Finished loading commands.");
	}
}
