package fr.eirb.caslogin.utils;

import com.google.gson.Gson;
import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.api.LoggedUser;
import fr.eirb.caslogin.api.body.ErrorBody;
import fr.eirb.caslogin.api.body.ValidateBody;
import fr.eirb.caslogin.exceptions.api.APIException;
import fr.eirb.caslogin.manager.ConfigurationManager;
import org.asynchttpclient.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ApiUtils {

	public static final String API_PATH = "/api";
	public static final String LOGIN_PATH = API_PATH + "/login.php";
	public static final String LOGOUT_PATH = API_PATH + "/logout.php";
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

	public static String getValidateUrl(Player p, String authCode){
		return ConfigurationManager.getAuthServer() + VALIDATE_PATH + "?code=" + authCode + "&uuid=" + p.getUniqueId().toString();
	}

	private static String getLogoutURL(LoggedUser loggedUser) {
		return ConfigurationManager.getAuthServer() + LOGOUT_PATH;
	}

	public static LoggedUser validateLogin(Player p, String authCode) throws APIException {
		try(AsyncHttpClient client = Dsl.asyncHttpClient()){
			Request req = getAuthorizedRequest().setUrl(getValidateUrl(p, authCode)).setMethod("GET").build();
			Response resp = client.executeRequest(req).get();
			if(resp.getStatusCode() != 200){
				handleApiError(resp);
			}
			Gson gsonInstance = new Gson();
			return gsonInstance.fromJson(resp.getResponseBody(), ValidateBody.class).getLoggedUser();
		} catch (IOException | ExecutionException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private static void handleApiError(Response resp) throws APIException {
		Gson gsonInstance = new Gson();
		ErrorBody body = gsonInstance.fromJson(resp.getResponseBody(), ErrorBody.class);
		throw new APIException(body.getError());
	}

	public static void logout(LoggedUser loggedUser) throws APIException{
		try(AsyncHttpClient client = Dsl.asyncHttpClient()){
			Request req = getAuthorizedRequest()
					.setUrl(getLogoutURL(loggedUser))
					.setMethod("POST")
					.setBody(String.format("{\"user\": \"%s\"}", loggedUser.getUser().getLogin()))
					.build();
			Response resp = client.executeRequest(req).get();
			if(resp.getStatusCode() != 200){
				handleApiError(resp);
			}
		} catch (IOException | ExecutionException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
