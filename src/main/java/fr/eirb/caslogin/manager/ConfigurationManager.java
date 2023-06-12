package fr.eirb.caslogin.manager;

import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.configuration.ConfigurationUtils;
import fr.eirb.caslogin.exceptions.configuration.AlreadyAdminException;
import fr.eirb.caslogin.exceptions.configuration.NotAdminException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class ConfigurationManager {

	private static final ConfigurationManager INSTANCE = new ConfigurationManager();

	private FileConfiguration pluginConfig;

	private FileConfiguration langConfig;

	private final File pluginConfigFile;

	private ConfigurationManager() {
		this.pluginConfigFile = new File(CasLogin.INSTANCE.getDataFolder(), "config.yml");
		CasLogin.INSTANCE.saveDefaultConfig();
		this.reloadConfig();
	}

	private void reloadConfig(){
		this.pluginConfig = CasLogin.INSTANCE.getConfig();
		this.langConfig = ConfigurationUtils.getOrCreateConfigurationFile("lang.yml");
	}

	public static void reload(){
		INSTANCE.reloadConfig();
	}

	public static String getLang(String path){
		return INSTANCE.langConfig.getString(path);
	}
}
