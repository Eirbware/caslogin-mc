package fr.eirb.caslogin.api.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.api.body.*;
import fr.eirb.caslogin.configuration.ConfigurationManager;
import fr.eirb.caslogin.exceptions.api.APIException;
import fr.eirb.caslogin.exceptions.api.Errors;
import fr.eirb.caslogin.exceptions.login.CouldNotGenerateCSRFTokenException;
import fr.eirb.caslogin.model.CasUser;
import fr.eirb.caslogin.model.LoggedUser;
import org.asynchttpclient.*;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

final class ApiUtils {

	public static final String API_PATH = "/api";
	public static final String LOGIN_PATH = API_PATH + "/login.php";
	public static final String LOGOUT_PATH = API_PATH + "/logout.php";
	public static final String GENCSRF_PATH = API_PATH + "/gen_csrf.php";
	public static final String BAN_PATH = API_PATH + "/bans.php";
	public static final String VALIDATE_PATH = API_PATH + "/validate.php";
	public static final String USERS_PATH = API_PATH + "/users.php";
	public static final String ROLES_PATH = API_PATH + "/roles.php";


	private static RequestBuilder getAuthorizedRequest() {
		return new RequestBuilder().addHeader("Authorization", "Bearer " + ConfigurationManager.getApiKey());
	}

	private static String getGenCsrfPath() {
		return ConfigurationManager.getAuthServer() + GENCSRF_PATH;
	}

	public static String getLoginUrl(Player p) throws CouldNotGenerateCSRFTokenException {
		return ConfigurationManager.getAuthServer() + LOGIN_PATH + "?token=" + generateTokenForPlayer(p);
	}

	private static String generateTokenForPlayer(Player p) throws CouldNotGenerateCSRFTokenException {
		try (AsyncHttpClient client = Dsl.asyncHttpClient()) {
			Request req = getAuthorizedRequest()
					.setUrl(getGenCsrfPath())
					.setMethod("POST")
					.setBody(String.format("{\"uuid\": \"%s\"}", p.getUniqueId().toString()))
					.build();
			Response resp = client.executeRequest(req).get();
			if (resp.getStatusCode() != 200) {
				handleApiError(resp);
			}
			Gson gsonInstance = new Gson();
			return gsonInstance.fromJson(resp.getResponseBody(), GenCsrfBody.class).getToken();
		} catch (IOException | ExecutionException | InterruptedException e) {
			throw new RuntimeException(e);
		} catch (APIException e) {
			if (e.error == Errors.COULD_NOT_GENERATE_CSRF)
				throw new CouldNotGenerateCSRFTokenException();
			throw new RuntimeException(e);
		}
	}

	public static String getValidateUrl(Player p, String authCode) {
		return ConfigurationManager.getAuthServer() + VALIDATE_PATH + "?code=" + authCode + "&uuid=" + p.getUniqueId().toString();
	}

	private static String getLogoutURL(LoggedUser loggedUser) {
		return ConfigurationManager.getAuthServer() + LOGOUT_PATH;
	}

	private static String getGetUserURL(UUID uuid) {
		return ConfigurationManager.getAuthServer() + USERS_PATH + "?uuid=" + uuid.toString();
	}

	private static String getGetUsersURL() {
		return ConfigurationManager.getAuthServer() + USERS_PATH;
	}

	private static String getBanUrl() {
		return ConfigurationManager.getAuthServer() + BAN_PATH;
	}

	public static LoggedUser validateLogin(Player p, String authCode) throws APIException {
		try (AsyncHttpClient client = Dsl.asyncHttpClient()) {
			Request req = getAuthorizedRequest().setUrl(getValidateUrl(p, authCode)).setMethod("GET").build();
			Response resp = client.executeRequest(req).get();
			if (resp.getStatusCode() != 200) {
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

	public static void logout(LoggedUser loggedUser) throws APIException {
		try (AsyncHttpClient client = Dsl.asyncHttpClient()) {
			Request req = getAuthorizedRequest()
					.setUrl(getLogoutURL(loggedUser))
					.setMethod("POST")
					.setBody(String.format("{\"user\": \"%s\"}", loggedUser.getUser().getLogin()))
					.build();
			Response resp = client.executeRequest(req).get();
			if (resp.getStatusCode() != 200) {
				handleApiError(resp);
			}
		} catch (IOException | ExecutionException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static LoggedUser getLoggedUser(UUID uuid) throws APIException {
		try (AsyncHttpClient client = Dsl.asyncHttpClient()) {
			Request req = getAuthorizedRequest().setUrl(getGetUserURL(uuid)).setMethod("GET").build();
			Response resp = client.executeRequest(req).get();
			if (resp.getStatusCode() != 200) {
				handleApiError(resp);
			}
			Gson gsonInstance = new Gson();
			return gsonInstance.fromJson(resp.getResponseBody(), GetUserBody.class).getUser();
		} catch (IOException | ExecutionException | InterruptedException | JsonSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private static List<GetUsersBody.CompositeUser> getCompositeUsers(AsyncHttpClient client) throws ExecutionException, InterruptedException, APIException {
		Request req = getAuthorizedRequest().setUrl(getGetUsersURL()).setMethod("GET").build();
		Response resp = client.executeRequest(req).get();
		if (resp.getStatusCode() != 200) {
			handleApiError(resp);
		}
		Gson gsonInstance = new Gson();
		return gsonInstance.fromJson(resp.getResponseBody(), GetUsersBody.class).getCompositeUsers();
	}

	public static List<LoggedUser> getLoggedUsers() throws APIException {
		try (AsyncHttpClient client = Dsl.asyncHttpClient()) {
			return getCompositeUsers(client)
					.stream()
					.filter(u -> u.uuid() != null)
					.map(u -> new LoggedUser(
							new CasUser(u.login(), u.ecole(), u.roles()),
							u.uuid()
					))
					.toList();
		} catch (IOException | ExecutionException | InterruptedException | JsonSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<CasUser> getCasUsers() throws APIException {
		try (AsyncHttpClient client = Dsl.asyncHttpClient()) {
			return getCompositeUsers(client)
					.stream()
					.map(u ->
							new CasUser(u.login(), u.ecole(), u.roles())
					)
					.toList();
		} catch (IOException | ExecutionException | InterruptedException | JsonSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public static void banUser(@Nullable CasUser banner, CasUser toBan, @Nullable String reason, @Nullable Duration banDuration) throws APIException {
		try (AsyncHttpClient client = Dsl.asyncHttpClient()) {
			Gson gson = new Gson();
			Request req = getAuthorizedRequest()
					.setUrl(getBanUrl())
					.setMethod("POST")
					.setBody(gson.toJson(new BanUserBody(banner, toBan, reason, banDuration)))
					.build();
			Response resp = client.executeRequest(req).get();
			if (resp.getStatusCode() != 200) {
				handleApiError(resp);
			}
		} catch (IOException | ExecutionException | InterruptedException | JsonSyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
