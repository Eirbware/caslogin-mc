package fr.kumakuma215.casloginfix.config;


import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ConfigurationManager {

	private static YamlConfiguration pluginConfig;

	private static File dataFolder;

	public static void loadConfig(File dataFolder) {
		ConfigurationManager.dataFolder = dataFolder;
		reloadConfig();
	}

	public static void reloadConfig() {
		pluginConfig = ConfigurationUtils.getOrCreateConfigurationFile(dataFolder, "config.yml");
	}

	public static String getSkinApiUrl() {
		return pluginConfig.getString("skinApiUrl");
	}

	private static class ConfigurationUtils {
		public static void tryCreatePluginConfigDir(File pluginDir) {
			if (!pluginDir.exists()) {
				if (!pluginDir.mkdirs()) {
					throw new RuntimeException("Cannot create configuration folder");
				}
			}
		}

		public static YamlConfiguration getOrCreateConfigurationFile(File pluginDir, String filename) {
			tryCreatePluginConfigDir(pluginDir);
			File configFile = new File(pluginDir, filename);
			if (!configFile.exists()) {
				try (InputStream in = ConfigurationUtils.class.getClassLoader().getResourceAsStream(filename)) {
					if (!configFile.createNewFile())
						throw new RuntimeException("Cannot create configuration file " + filename);
					assert in != null;
					Files.copy(in, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			return YamlConfiguration.loadConfiguration(configFile);
		}
	}
}
