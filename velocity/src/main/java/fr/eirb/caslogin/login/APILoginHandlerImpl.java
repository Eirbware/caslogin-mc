package fr.eirb.caslogin.login;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.GameProfile;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.api.body.*;
import fr.eirb.caslogin.api.model.LoggedUser;
import fr.eirb.caslogin.exceptions.api.APIException;
import fr.eirb.caslogin.exceptions.api.Errors;
import fr.eirb.caslogin.exceptions.login.AlreadyLoggingInException;
import fr.eirb.caslogin.exceptions.login.CouldNotGenerateCSRFTokenException;
import fr.eirb.caslogin.exceptions.login.NotLoggedInException;
import fr.eirb.caslogin.configuration.ConfigurationManager;
import fr.eirb.caslogin.utils.PlayerUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.asynchttpclient.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

class APILoginHandlerImpl implements LoginHandler {
	private static final Set<Player> loggingPlayer = Collections.synchronizedSet(new HashSet<>());

	@Override
	public CompletableFuture<List<LoggedUser>> getLoggedUsers() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				List<LoggedUser> users = ApiUtils.getLoggedUsers();
				for (LoggedUser user : users) {
					CasLogin.get().getLoginDatabase().put(user.getUuid(), user);
				}
				return users;
			} catch (APIException e) {
				throw new IllegalStateException(e);
			}
		});
	}

	@Override
	public CompletableFuture<LoggedUser> login(Player player) {
		return pollLogin(player, ConfigurationManager.getLoginPollTimeoutSeconds(), ConfigurationManager.getLoginPollIntervalMS());
	}

	private CompletableFuture<LoggedUser> getLoggedUserFromPlayerOrThrow(Player player) {
		return CasLogin.get().getLoginDatabase()
				.get(PlayerUtils.getTrueIdentity(player).getId())
				.thenCompose(optionalLoggedUser -> {
					if (optionalLoggedUser.isEmpty())
						return CompletableFuture.failedFuture(new CompletionException(new NotLoggedInException(player)));
					LoggedUser loggedUser = optionalLoggedUser.get();
					return CompletableFuture.completedFuture(loggedUser);
				});
	}

	@Override
	public CompletableFuture<Void> logout(Player player) {
		return getLoggedUserFromPlayerOrThrow(player)
				.thenCompose((loggedUser -> CompletableFuture.runAsync(() -> {
					try {
						ApiUtils.logout(loggedUser);
					} catch (APIException e) {
						if (e.error == Errors.USER_NOT_LOGGED_IN) {
							throw new CompletionException(new NotLoggedInException(player));
						} else {
							throw new IllegalStateException(e);
						}
					}
				})));
	}

	private CompletableFuture<LoggedUser> pollLogin(Player player, int timeoutSeconds, long intervalSeconds) {
		if (loggingPlayer.contains(player))
			return CompletableFuture.failedFuture(new AlreadyLoggingInException());
		return getLoggedUserFromPlayerOrThrow(player)
				.exceptionallyCompose(throwable -> {
					loggingPlayer.add(player);
					CasLogin.get().getLogger().info(String.format("Starting logging poll for player '%s'", player.getUsername()));
					return CompletableFuture.supplyAsync(() -> {
								long counter = 0;
								while (counter < timeoutSeconds) {
									LoggedUser user;
									try {
										user = ApiUtils.getLoggedUser(player.getUniqueId());

									} catch (APIException e) {
										CasLogin.get().getLogger().severe("API EXCEPTION ON LOGIN POLL! Something is really wrong.");
										loggingPlayer.remove(player);
										throw new IllegalStateException(e);
									}
									if (user != null) {
										return user;
									}
									try {
										TimeUnit.SECONDS.sleep(intervalSeconds);
									} catch (InterruptedException e) {
										throw new IllegalStateException(e);
									}
									counter += intervalSeconds;
								}
								CasLogin.get().getLogger().info(String.format("Polling timed out for player '%s'", player.getUsername()));
								loggingPlayer.remove(player);
								player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("errors.login_timeout")));
								return null;
							})
							.thenCompose(loggedUser -> CasLogin.get().getLoginDatabase()
									.put(player.getUniqueId(), loggedUser)
									.thenAccept(ignored -> {
										CasLogin.get().getLogger().info(String.format("Player '%s' logged as '%s'.", player.getUsername(), loggedUser.getUser().getLogin()));
										loggingPlayer.remove(player);
									})
									.thenApply(unused -> loggedUser)
							);
				});
	}

	private static class ApiUtils {

		public static final String API_PATH = "/api";
		public static final String LOGIN_PATH = API_PATH + "/login.php";
		public static final String LOGOUT_PATH = API_PATH + "/logout.php";
		public static final String GENCSRF_PATH = API_PATH + "/gen_csrf.php";
		public static final String BAN_PATH = API_PATH + "/ban.php";
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

		public static List<LoggedUser> getLoggedUsers() throws APIException {
			try (AsyncHttpClient client = Dsl.asyncHttpClient()) {
				Request req = getAuthorizedRequest().setUrl(getGetUsersURL()).setMethod("GET").build();
				Response resp = client.executeRequest(req).get();
				if (resp.getStatusCode() != 200) {
					handleApiError(resp);
				}
				Gson gsonInstance = new Gson();
				return gsonInstance.fromJson(resp.getResponseBody(), GetUsersBody.class).getUsers();
			} catch (IOException | ExecutionException | InterruptedException | JsonSyntaxException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
