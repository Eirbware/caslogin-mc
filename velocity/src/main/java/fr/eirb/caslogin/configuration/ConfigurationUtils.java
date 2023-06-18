package fr.eirb.caslogin.configuration;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ConfigurationUtils {
	public static void tryCreatePluginConfigDir(Path pluginDir){
		if(!Files.exists(pluginDir)) {
			try {
				Files.createDirectory(pluginDir);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	public static ConfigurationNode getOrCreateConfigurationFile(Path pluginDir, String filename){
		tryCreatePluginConfigDir(pluginDir);
		Path configFile = pluginDir.resolve(filename);
		if(!Files.exists(configFile)) {
			try(InputStream in = ConfigurationUtils.class.getClassLoader().getResourceAsStream(filename)){
				Files.createFile(configFile);
				assert in != null;
				Files.copy(in, configFile, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		try {
			return YAMLConfigurationLoader.builder().setFile(configFile.toFile()).build().load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
