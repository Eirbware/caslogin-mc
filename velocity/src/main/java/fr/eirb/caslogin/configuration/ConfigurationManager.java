package fr.eirb.caslogin.configuration;

import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.exceptions.configuration.NoSuchLoginHandler;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
		return pluginConfig.getNode("entrypoint_server").getString();
	}
	public static String getLoggedEntrypointServer() {
		return pluginConfig.getNode("logged_entrypoint_server").getString();
	}
	public static int getLoginPollTimeoutSeconds(){
		return pluginConfig.getNode("loginPollTimeoutSeconds").getInt();
	}
	public static long getLoginPollIntervalMS(){
		return pluginConfig.getNode("loginPollIntervalMS").getLong();
	}
	public static LoginHandlerTypes getLoginHandlerType() throws NoSuchLoginHandler{
		String type = pluginConfig.getNode("loginHandlerType").getString();
		try {
			return LoginHandlerTypes.valueOf(type);
		}catch(IllegalArgumentException ex){
			throw new NoSuchLoginHandler(type);
		}
	}

	private static class ConfigurationUtils {
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
}
