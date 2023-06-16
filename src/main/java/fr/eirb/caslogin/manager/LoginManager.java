package fr.eirb.caslogin.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.api.LoggedUser;
import fr.eirb.caslogin.exceptions.login.*;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.utils.ApiUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public final class LoginManager {

	public static BiMap<Player, LoggedUser> loggedUserMap = HashBiMap.create();

	public static LoggedUser logPlayer(Player p) throws AlreadyLoggedInException, LoginAlreadyTakenException {

		return null;
	}

	public static void logout(Player p) throws NotLoggedInException {

	}

}
