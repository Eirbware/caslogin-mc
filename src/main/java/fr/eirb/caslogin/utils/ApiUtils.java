package fr.eirb.caslogin.utils;

import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.api.LoggedUser;
import fr.eirb.caslogin.manager.ConfigurationManager;
import org.asynchttpclient.RequestBuilder;

import java.io.IOException;
import java.util.Set;

public class ApiUtils {

	public static final String API_PATH = "/api";
	public static final String LOGIN_PATH = API_PATH + "/login.php";
	public static final String BAN_PATH = API_PATH + "/ban.php";
	public static final String VALIDATE_PATH = API_PATH + "/validate.php";
	public static final String USERS_PATH = API_PATH + "/users.php";
	public static final String ROLES_PATH = API_PATH + "/roles.php";


	private static RequestBuilder getAuthorizedRequest(){
		return new RequestBuilder().addHeader("Authorization", "Bearer " + ConfigurationManager.getApiKey());
	}

	public static String getLoginUrl(Player p){
		return ConfigurationManager.getAuthServer() + LOGIN_PATH + "?uuid=" + p.getUniqueId().toString();
	}
}
