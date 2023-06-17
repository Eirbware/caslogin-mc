package fr.eirb.caslogin.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.api.LoggedUser;
import fr.eirb.caslogin.exceptions.api.APIException;
import fr.eirb.caslogin.exceptions.api.Errors;
import fr.eirb.caslogin.exceptions.login.*;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.utils.ApiUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public final class LoginManager {

	public static BiMap<UUID, LoggedUser> loggedUserMap = HashBiMap.create();

	public static LoggedUser logPlayer(Player p, String authCode) throws LoginAlreadyTakenException,
			InvalidAuthCodeException, AuthCodeExpiredException, InvalidTokenException, NoAuthCodeForUuidException {
		try {
			LoggedUser user = ApiUtils.validateLogin(p, authCode);
			loggedUserMap.put(p.getUniqueId(), user);
			return user;
		} catch (APIException ex) {
			switch (ex.error) {
				case USER_ALREADY_LOGGED_IN -> throw new LoginAlreadyTakenException();
				case INVALID_AUTH_CODE -> throw new InvalidAuthCodeException(authCode, p);
				case AUTH_CODE_EXPIRED -> throw new AuthCodeExpiredException(authCode, p);
				case INVALID_TOKEN -> throw new InvalidTokenException();
				case NO_AUTH_CODE_FOR_UUID -> throw new NoAuthCodeForUuidException();
				default -> throw new RuntimeException(ex);
			}
		}
	}

	public static void logout(Player p) throws NotLoggedInException {
		if(!loggedUserMap.containsKey(p.getUniqueId())) {
			throw new NotLoggedInException(p);
		}
		try{
			ApiUtils.logout(loggedUserMap.get(p.getUniqueId()));
			loggedUserMap.remove(p.getUniqueId());
		}catch(APIException ex){
			if(ex.error == Errors.USER_NOT_LOGGED_IN)
				throw new NotLoggedInException(p);
			throw new RuntimeException(ex);
		}
	}

}
