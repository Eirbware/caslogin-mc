package fr.eirb.caslogin.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.eirb.caslogin.exceptions.LoginAlreadyTakenException;
import fr.eirb.caslogin.exceptions.LoginException;
import fr.eirb.caslogin.exceptions.NotLoggedInException;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.exceptions.AlreadyLoggedInException;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public final class LoginManager {

	public static final LoginManager INSTANCE = new LoginManager();
	private static final String LOGGED_PLAYERS_FILENAME = "logged.json";
	private static final String BANNED_USERS_FILENAME = "banned.json";
	private final BiMap<UUID, String> loggedPlayers;

	private final Set<String> bannedUsers;

	private final File loggedPlayersFile;

	private final File bannedUsersFile;

	private final Gson gsonInstance;

	private LoginManager() {
		gsonInstance = new Gson();
		this.loggedPlayersFile = new File(CasLogin.INSTANCE.getDataFolder(), LOGGED_PLAYERS_FILENAME);
		this.bannedUsersFile = new File(CasLogin.INSTANCE.getDataFolder(), BANNED_USERS_FILENAME);
		if (!loggedPlayersFile.exists()) {
			loggedPlayersFile.getParentFile().mkdirs();
			CasLogin.INSTANCE.saveResource(LOGGED_PLAYERS_FILENAME, false);
		}
		if (!bannedUsersFile.exists()) {
			bannedUsersFile.getParentFile().mkdirs();
			CasLogin.INSTANCE.saveResource(BANNED_USERS_FILENAME, false);
		}


		loggedPlayers = loadLoggedPlayers();
		bannedUsers = loadBannedPlayers();

	}

	public void logPlayer(Player p, String login) throws LoginException {
		if (isLoggedIn(p)) {
			throw new AlreadyLoggedInException(p);
		}
		if (isLoggedIn(login)) {
			throw new LoginAlreadyTakenException(login);
		}
		loggedPlayers.put(p.getUniqueId(), login);
		saveToJson();
	}

	public void logout(Player p) throws NotLoggedInException {
		logout(loggedPlayers.get(p.getUniqueId()));
	}

	public void logout(String login) throws NotLoggedInException {
		if(!loggedPlayers.values().remove(login))
			throw new NotLoggedInException(login);
		saveToJson();
	}

	public Set<String> getLoggedCASAccounts() {
		return loggedPlayers.values();
	}

	public OfflinePlayer getLoggedPlayer(String login) throws NotLoggedInException{
		if(!loggedPlayers.containsValue(login))
			throw new NotLoggedInException(login);
		return CasLogin.INSTANCE.getServer().getOfflinePlayer(loggedPlayers.inverse().get(login));
	}

	public boolean isLoggedIn(Player p) {
		return loggedPlayers.containsKey(p.getUniqueId());
	}

	public boolean isLoggedIn(UUID uuid) {
		return loggedPlayers.containsKey(uuid);
	}

	public boolean isLoggedIn(String login) {
		return loggedPlayers.containsValue(login);
	}

	public String getLogin(Player p) {
		return loggedPlayers.get(p.getUniqueId());
	}

	public String getLogin(UUID uuid) {
		return loggedPlayers.get(uuid);
	}

	private void saveToJson() {
		try (FileWriter writer = new FileWriter(loggedPlayersFile)) {
			gsonInstance.toJson(loggedPlayers, writer);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	private BiMap<UUID, String> loadLoggedPlayers() {
		try (FileReader reader = new FileReader(loggedPlayersFile)) {

			Map<UUID, String> map = gsonInstance.fromJson(reader, TypeToken.getParameterized(HashMap.class, UUID.class, String.class).getType());
			return HashBiMap.create(map);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	private Set<String> loadBannedPlayers() {
		try (FileReader reader = new FileReader(bannedUsersFile)) {
			return gsonInstance.fromJson(reader, TypeToken.getParameterized(HashSet.class, String.class).getType());
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}


}
