package fr.eirb.caslogin.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.exceptions.login.*;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.utils.ApiUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public final class LoginManager {

	public static final LoginManager INSTANCE = new LoginManager();
	private final BiMap<UUID, String> loggedPlayers;

	private final Gson gsonInstance;

	private LoginManager() {
		gsonInstance = new Gson();


		loggedPlayers = loadLoggedPlayers();

	}

	private BiMap<UUID, String> loadLoggedPlayers() {
		return null;
	}

	public void logPlayer(Player p, String login) throws LoginException {
		if (isLoggedIn(p)) {
			throw new AlreadyLoggedInException(p);
		}
		if (isLoggedIn(login)) {
			throw new LoginAlreadyTakenException(login);
		}
		loggedPlayers.put(p.getUniqueId(), login);
	}

	public void logout(Player p) throws NotLoggedInException {
		logout(loggedPlayers.get(p.getUniqueId()));
	}

	public void logout(String login) throws NotLoggedInException {
		if(!loggedPlayers.values().remove(login))
			throw new NotLoggedInException(login);
	}

	public Set<String> getLoggedCASAccounts() {
		return Collections.unmodifiableSet(loggedPlayers.values());
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
		return getLogin(p.getUniqueId());
	}

	public String getLogin(UUID uuid) {
		return loggedPlayers.get(uuid);
	}

}
