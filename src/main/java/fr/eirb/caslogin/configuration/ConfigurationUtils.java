package fr.eirb.caslogin.configuration;

import fr.eirb.caslogin.CasLogin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigurationUtils {
	public static YamlConfiguration getOrCreateConfigurationFile(String filename){
		File customConfigFile = new File(CasLogin.INSTANCE.getDataFolder(), filename);
		if(!customConfigFile.exists()){
			customConfigFile.getParentFile().mkdirs();
			CasLogin.INSTANCE.saveResource(filename, false);
		}

		return YamlConfiguration.loadConfiguration(customConfigFile);
	}

	public static YamlConfiguration getOrCreateConfigurationFile(File customConfigFile){
		if(!customConfigFile.exists()){
			customConfigFile.getParentFile().mkdirs();
			CasLogin.INSTANCE.saveResource(customConfigFile.getName(), false);
		}

		return YamlConfiguration.loadConfiguration(customConfigFile);
	}
}
