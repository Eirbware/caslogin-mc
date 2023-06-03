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

	private final FileConfiguration pluginConfig;

	private final FileConfiguration adminConfig;

	private final FileConfiguration langConfig;

	private final File adminConfigFile;
	private final File pluginConfigFile;

	private final List<String> adminsCache;

	private ConfigurationManager(){
		this.adminConfigFile = new File(CasLogin.INSTANCE.getDataFolder(), "admins.yml");
		this.pluginConfigFile = new File(CasLogin.INSTANCE.getDataFolder(), "config.yml");
		CasLogin.INSTANCE.saveDefaultConfig();
		this.pluginConfig = CasLogin.INSTANCE.getConfig();
		this.adminConfig = ConfigurationUtils.getOrCreateConfigurationFile(adminConfigFile);
		this.langConfig = ConfigurationUtils.getOrCreateConfigurationFile("lang.yml");
		this.adminsCache = adminConfig.getStringList("admins");
	}

	public static List<String> getAdmins(){
		return Collections.unmodifiableList(INSTANCE.adminsCache);
	}

	public static void addAdmin(String login) throws AlreadyAdminException {
		if(INSTANCE.adminsCache.contains(login)){
			throw new AlreadyAdminException(login);
		}
		INSTANCE.adminsCache.add(login);
		INSTANCE.adminConfig.set("admins", INSTANCE.adminsCache);
		try {
			INSTANCE.adminConfig.save(INSTANCE.adminConfigFile);
		}catch(IOException ex){
			CasLogin.INSTANCE.getLogger().log(Level.SEVERE, "Cannot save to admin.yml");
			throw new RuntimeException(ex);
		}
	}

	public static void removeAdmin(String login) throws NotAdminException {
		if(!INSTANCE.adminsCache.contains(login))
			throw new NotAdminException(login);
		INSTANCE.adminsCache.remove(login);
		INSTANCE.adminConfig.set("admins", INSTANCE.adminsCache);
		try{
			INSTANCE.adminConfig.save(INSTANCE.adminConfigFile);
		}catch(IOException ex){
			CasLogin.INSTANCE.getLogger().log(Level.SEVERE, "Cannot save to admin.yml");
			throw new RuntimeException(ex);
		}
	}

	public static String getLang(String path){
		return INSTANCE.langConfig.getString(path);
	}
}
