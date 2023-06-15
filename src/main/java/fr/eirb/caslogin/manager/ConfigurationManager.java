package fr.eirb.caslogin.manager;

import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.configuration.ConfigurationUtils;
import fr.eirb.caslogin.exceptions.configuration.AlreadyAdminException;
import fr.eirb.caslogin.exceptions.configuration.NotAdminException;
import ninja.leaping.configurate.ConfigurationNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class ConfigurationManager {

	private static ConfigurationNode pluginConfig;

	private static ConfigurationNode langConfig;

	public static void reloadConfig(Path dataFolder){
		pluginConfig = ConfigurationUtils.getOrCreateConfigurationFile(dataFolder, "config.yml");
		langConfig = ConfigurationUtils.getOrCreateConfigurationFile(dataFolder, "lang.yml");
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
}
