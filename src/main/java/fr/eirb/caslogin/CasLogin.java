package fr.eirb.caslogin;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.eirb.caslogin.commands.CasCommand;
import fr.eirb.caslogin.manager.ConfigurationManager;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(
		id = Constants.PLUGIN_ID,
		name = Constants.PLUGIN_NAME,
		version = Constants.VERSION

)
public class CasLogin {
	@Inject
	private Logger logger;

	@Inject
	private ProxyServer proxy;

	@Inject
	public CasLogin(@DataDirectory Path pluginDir){
		ConfigurationManager.reloadConfig(pluginDir);
	}
	@Subscribe
	public void onProxyInit(ProxyInitializeEvent ev){
		logger.info("Loading plugin...");
		registerCommands();
		logger.info("Plugin successfully loaded!");
	}

	@Subscribe(order = PostOrder.FIRST)
	public void disableServerCommand(PermissionsSetupEvent ev){
		ev.setProvider(subject -> permission -> {
			if(subject instanceof ConsoleCommandSource)
				return Tristate.TRUE;
			if(permission.equals("velocity.command.server"))
				return Tristate.FALSE;
			return Tristate.UNDEFINED;
		});
	}

	private void registerCommands() {
		logger.info("Loading commands...");
		proxy.getCommandManager().register(CasCommand.createCasCommand(proxy));
		logger.info("Finished loading commands.");
	}
}
