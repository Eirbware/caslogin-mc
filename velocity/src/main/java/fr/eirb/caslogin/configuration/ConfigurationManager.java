package fr.eirb.caslogin.configuration;

import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.exceptions.configuration.NoSuchLoginHandler;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

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
		return langConfig.node((Object[]) path.split("\\.")).getString();
	}
	public static String getAuthServer(){
		return pluginConfig.node("auth_server").getString();
	}
	public static String getApiKey(){
		return pluginConfig.node("api_key").getString();
	}
	public static String getEntrypointServerName() {
		return pluginConfig.node("entrypoint_server").getString();
	}
	public static String getLoggedEntrypointServer() {
		return pluginConfig.node("logged_entrypoint_server").getString();
	}
	public static int getLoginPollTimeoutSeconds(){
		return pluginConfig.node("loginPollTimeoutSeconds").getInt();
	}
	public static long getLoginPollIntervalMS(){
		return pluginConfig.node("loginPollIntervalMS").getLong();
	}
	public static LoginHandlerTypes getLoginHandlerType() throws NoSuchLoginHandler{
		String type = pluginConfig.node("loginHandlerType").getString();
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
				return YamlConfigurationLoader.builder().file(configFile.toFile()).build().load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
