package fr.kumakuma215.proxyutils;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.kumakuma215.proxyutils.commands.AlertCommand;
import org.slf4j.Logger;

@Plugin(
		id = "proxyutils",
		name = "proxyutils",
		version = BuildConstants.VERSION
)
public class Proxyutils {

	@Inject
	private Logger logger;

	@Inject
	private ProxyServer server;

	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent event) {
		server.getCommandManager().register(AlertCommand.createAlertCommand(server));
	}
}
