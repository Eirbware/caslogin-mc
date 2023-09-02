package fr.eirb.caslogin.manager;

import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.configuration.ConfigurationUtils;
import ninja.leaping.configurate.ConfigurationNode;

import java.nio.file.Path;

public class ConfigurationManager {

	private static ConfigurationNode pluginConfig;

	private static ConfigurationNode langConfig;

	private static Path dataFolder;

	public static void loadConfig(Path dataFolder){
		ConfigurationManager.dataFolder = dataFolder;
		reloadConfig();
	}

	public static void reloadConfig(){
		pluginConfig = ConfigurationUtils.getOrCreateConfigurationFile(dataFolder, "config.yml");
		langConfig = ConfigurationUtils.getOrCreateConfigurationFile(dataFolder, "lang.yml");
		LoginManager.resetLoggedUsers();
		CasLogin.resetEntrypoints();
	}


	public static String getLang(String path){
		return langConfig.getNode((Object[]) path.split("\\.")).getString();
	}
	public static String getAuthServer(){
		return pluginConfig.getNode("auth_server").getString();
	}

	public static String getApiKey(){
		return pluginConfig.getNode("api_key").getString();
	}

	public static String getEntrypointServerName() {
		return pluginConfig.getNode("limbo_server").getString();
	}

	public static String getLoggedEntrypointServer() {
		return pluginConfig.getNode("logged_server").getString();
	}
}
