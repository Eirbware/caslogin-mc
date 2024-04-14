package fr.eirb.caslogin;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fr.eirb.caslogin.commands.CasCommand;
import fr.eirb.caslogin.configuration.ConfigurationManager;
import fr.eirb.caslogin.listeners.*;
import fr.eirb.caslogin.login.*;
import fr.eirb.caslogin.model.LoggedUser;
import fr.eirb.caslogin.role.RoleManager;
import fr.eirb.caslogin.role.impl.DummyRoleManager;
import fr.eirb.caslogin.role.impl.LuckPermsRoleManager;
import fr.eirb.caslogin.utils.PlayerUtils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

import java.nio.file.Path;
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

	private static CasLogin INSTANCE;
	private final Path pluginDir;
	@Inject
	private Logger logger;
	@Inject
	private ProxyServer proxy;
	private RoleManager roleManager;
	private static RegisteredServer entrypointServer;
	private static RegisteredServer loggedEntrypointServer;
	private LoginHandler loginHandler;
	private LoginDatabase loginDatabase;

	@Inject
	public CasLogin(@DataDirectory Path pluginDir) {
		this.pluginDir = pluginDir;
	}

	public static CasLogin get() {
		return INSTANCE;
	}

	public ProxyServer getProxy() {
		return proxy;
	}

	public LoginHandler getLoginHandler() {
		return loginHandler;
	}

	@Subscribe
	public void onProxyInit(ProxyInitializeEvent ev) {
		logger.info("Loading plugin...");
		INSTANCE = this;
		ConfigurationManager.loadConfig(pluginDir);
		if(!getProxy().getConfiguration().isOnlineMode())
			logger.info("Offline mode activated");
		resetEntrypoints();
		registerCommands();
		registerDatabases();
		registerListeners();
		hookLuckperms();
		createLoginHandler();
		// Load cache
		logger.info("Plugin successfully loaded!");
	}

	private void registerListeners() {
		proxy.getEventManager().register(this, new PlayerUtils());
		proxy.getEventManager().register(this, new AvoidDoublePlayersListener());
		if(proxy.getConfiguration().isOnlineMode())
			proxy.getEventManager().register(this, new AutoLoginListener());
		if(!proxy.getConfiguration().isOnlineMode()) {
			proxy.getEventManager().register(this, new AutoLogoutListener());
		}
		proxy.getEventManager().register(this, new RoleUpdaterListener());
		proxy.getEventManager().register(this, new UpdateServerFieldsListener());
		proxy.getEventManager().register(this, new SendMessageForFixesListener());
	}

	private void registerDatabases() {
		this.loginDatabase = new MemoryLoginDatabase();
	}

	private void createLoginHandler() {
		loginHandler = switch (ConfigurationManager.getLoginHandlerType()) {
			case API -> LoginHandlerFactory.getAPILoginHandler();
		};
	}

	public static void resetEntrypoints() {
		entrypointServer = INSTANCE.proxy.getServer(ConfigurationManager.getEntrypointServerName()).orElseThrow();
		loggedEntrypointServer = INSTANCE.proxy.getServer(ConfigurationManager.getLoggedEntrypointServer()).orElseThrow();
	}

	private void hookLuckperms() {
		try {
			LuckPerms api = LuckPermsProvider.get();
			this.roleManager = new LuckPermsRoleManager(api);
			logger.info("Found LuckPerms. Loading LuckPerms RoleManager...");
		} catch (IllegalStateException notLoaded) {
			logger.warning("Could not find LuckPerms. Using dummy rolemanager that does nothing.");
			this.roleManager = new DummyRoleManager();
		}
	}


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

	public static RegisteredServer getEntrypointServer() {
		return entrypointServer;
	}

	public static RegisteredServer getLoggedEntrypointServer() {
		return loggedEntrypointServer;
	}

	public LoginDatabase getLoginDatabase() {
		return loginDatabase;
	}

	public void refresh() {
		ConfigurationManager.reloadConfig();
		resetEntrypoints();
		loginHandler
				.getLoggedUsers()
				.thenAccept(loggedUsers -> {
					for (LoggedUser user : loggedUsers) {
						loginDatabase.put(user.getUuid(), user);
					}
				});
	}
}
