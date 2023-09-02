package fr.eirb.caslogin;

import com.google.common.base.Charsets;
import com.google.inject.Inject;
import com.velocitypowered.api.command.VelocityBrigadierMessage;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
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
import fr.eirb.caslogin.manager.RoleManager;
import fr.eirb.caslogin.manager.impl.DummyRoleManager;
import fr.eirb.caslogin.manager.impl.LuckPermsRoleManager;
import fr.eirb.caslogin.utils.PlayerUtils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Logger;

@Plugin(
		id = Constants.PLUGIN_ID,
		name = Constants.PLUGIN_NAME,
		version = Constants.VERSION,
		dependencies = {
				@Dependency(id = "luckperms", optional = true)
		}
)
public class CasLogin {

	private static final ChannelIdentifier CAS_FIX_CHANNEL = MinecraftChannelIdentifier.create("caslogin", "auth");

	private static CasLogin INSTANCE;

	@Inject
	private Logger logger;

	@Inject
	private ProxyServer proxy;

	private RoleManager roleManager;

	@Inject
	public CasLogin(@DataDirectory Path pluginDir) {
		ConfigurationManager.loadConfig(pluginDir);
	}

	public static CasLogin getINSTANCE() {
		return INSTANCE;
	}

	@Subscribe
	public void onProxyInit(ProxyInitializeEvent ev) {
		logger.info("Loading plugin...");
		INSTANCE = this;
		registerCommands();
		hookLuckperms();
		LoginManager.cacheLoggedUsers();
		logger.info("Plugin successfully loaded!");
	}

	private void hookLuckperms() {
		try {
			LuckPerms api = LuckPermsProvider.get();
			this.roleManager = new LuckPermsRoleManager(api, proxy);
			logger.info("Found LuckPerms. Loading LuckPerms RoleManager...");
		} catch (IllegalStateException notLoaded) {
			logger.warning("Could not find LuckPerms. Using dummy rolemanager that does nothing.");
			this.roleManager = new DummyRoleManager();
		}
	}

	@Subscribe
	public void onServerChange(ServerPostConnectEvent ev) {
		Player player = ev.getPlayer();
		if (player.getCurrentServer().isEmpty())
			return;
		RegisteredServer currentServer = player.getCurrentServer().get().getServer();
		if (!currentServer.getServerInfo().getName().equals(ConfigurationManager.getLimboServerName())) {
			return;
		}
		LoginManager.getLoggedPlayer(player)
				.ifPresent((loggedUser) -> {
					logger.info(String.format("Player '%s' is logged in as '%s'. Moving them.", player.getUsername(), loggedUser.getUser().getLogin()));
					LoginManager.moveLoggedPlayer(player, proxy, loggedUser);
				});
	}

	@Subscribe
	private void updateRolesOnLogin(PostLoginEvent ev) {
		logger.info("Updating roles for user '" + ev.loggedUser().getUser().getLogin() + "'");
		roleManager.updateUserRoles(ev.loggedUser());
	}

	@Subscribe
	private void sendPluginMessageForFixes(PostLoginEvent ev) {
		Player player = ev.player();
		LoggedUser userForPlayer = ev.loggedUser();
		ServerConnection conn = player.getCurrentServer().orElseThrow();
		String message = UuidUtils.generateOfflinePlayerUuid(player.getUsername()) + ":" + userForPlayer.getFakeUserUUID();
		logger.info(String.format("Sending '%s' at '%s' to server '%s'", message, CAS_FIX_CHANNEL, conn.getServerInfo().getName()));
		conn.sendPluginMessage(CAS_FIX_CHANNEL, Charsets.UTF_8.encode(message).array());
	}

//	@Subscribe
//	public void onDisconnect(DisconnectEvent ev) {
//		logger.info("Logging out player " + ev.getPlayer().getUsername());
//		try {
//			LoginManager.logout(ev.getPlayer());
//
//		} catch (NotLoggedInException ignored) {
//		}
//
//	}


	private void registerCommands() {
		logger.info("Loading commands...");
		proxy.getCommandManager().register(CasCommand.createCasCommand(proxy));
		logger.info("Finished loading commands.");
	}

	public RoleManager getRoleManager() {
		return roleManager;
	}

	public Logger getLogger() {
		return logger;
	}
}
