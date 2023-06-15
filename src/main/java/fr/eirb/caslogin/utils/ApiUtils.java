package fr.eirb.caslogin.utils;

import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.manager.ConfigurationManager;

import java.io.IOException;
import java.util.Set;

public class ApiUtils {

//	public static final String API_PATH = "/api";
//	public static final String LOGIN_PATH = API_PATH + "/login.php";
//	public static final String BAN_PATH = API_PATH + "/ban.php";
//	public static final String VALIDATE_PATH = API_PATH + "/validate.php";
//	public static final String USERS_PATH = API_PATH + "/users.php";
//	public static final String ROLES_PATH = API_PATH + "/roles.php";
//
//
//	private static Request.Builder getAuthorizedRequest(){
//		return new Request.Builder().addHeader("Authorization", "Bearer " + ConfigurationManager.getApiKey());
//	}
//	public static Set<String> getBannedUsers(){
//		OkHttpClient client = new OkHttpClient();
//		Request req = getAuthorizedRequest().url(ConfigurationManager.getAuthServer() + BAN_PATH).get().build();
//		try(Response response = client.newCall(req).execute()){
//			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//
//
//			assert response.body() != null;
//			System.out.println(response.body().string());
//		}catch(IOException ex){
//			ex.printStackTrace();
//		}
//
//		return null;
//	}
}
