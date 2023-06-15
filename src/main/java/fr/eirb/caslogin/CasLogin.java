package fr.eirb.caslogin;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
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

	}
}
