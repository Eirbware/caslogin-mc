package fr.eirb.caslogin.api.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.api.body.*;
import fr.eirb.caslogin.login.LoginHandler;
import fr.eirb.caslogin.model.LoggedUser;
import fr.eirb.caslogin.configuration.ConfigurationManager;
import fr.eirb.caslogin.exceptions.api.APIException;
import fr.eirb.caslogin.exceptions.api.Errors;
import fr.eirb.caslogin.exceptions.login.AlreadyLoggingInException;
import fr.eirb.caslogin.exceptions.login.CouldNotGenerateCSRFTokenException;
import fr.eirb.caslogin.exceptions.login.LoginTimeoutException;
import fr.eirb.caslogin.exceptions.login.NotLoggedInException;
import fr.eirb.caslogin.utils.PlayerUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.asynchttpclient.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class APILoginHandlerImpl implements LoginHandler {
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
		try {
			String loginUrl = ApiUtils.getLoginUrl(player);
			String message = String.format(ConfigurationManager.getLang("user.login.url_message"), loginUrl, loginUrl);
			player.sendMessage(MiniMessage.miniMessage().deserialize(message));
		} catch (CouldNotGenerateCSRFTokenException e) {
			player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.could_not_generate_csrf")));
			return CompletableFuture.failedFuture(e);
		}
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
	public CompletableFuture<LoggedUser> logout(LoggedUser loggedUser) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				ApiUtils.logout(loggedUser);
				return loggedUser;
			} catch (APIException e) {
				if (e.error == Errors.USER_NOT_LOGGED_IN) {
					throw new CompletionException(new NotLoggedInException(loggedUser));
				} else {
					throw new IllegalStateException(e);
				}
			}
		}).thenApply(user -> {
			CasLogin.get().getLoginDatabase().remove(loggedUser.getUuid());
			return user;
		});
	}

	@Override
	public CompletableFuture<LoggedUser> logout(Player player) {
		return getLoggedUserFromPlayerOrThrow(player)
				.thenCompose(this::logout);
	}

	private CompletableFuture<LoggedUser> pollLogin(Player player, int timeoutSeconds, long intervalMS) {
		long timeoutMs = timeoutSeconds * 1000L;
		if (loggingPlayer.contains(player))
			return CompletableFuture.failedFuture(new AlreadyLoggingInException());
		return getLoggedUserFromPlayerOrThrow(player)
				.exceptionallyCompose(throwable -> CompletableFuture.supplyAsync(() -> {
							loggingPlayer.add(player);
							CasLogin.get().getLogger().info(String.format("Starting logging poll for player '%s'", player.getUsername()));
							long counter = 0;
							while (counter < timeoutMs) {
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
									TimeUnit.MILLISECONDS.sleep(intervalMS);
								} catch (InterruptedException e) {
									throw new IllegalStateException(e);
								}
								counter += intervalMS;
							}
							CasLogin.get().getLogger().info(String.format("Polling timed out for player '%s'", player.getUsername()));
							loggingPlayer.remove(player);
							player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("errors.login_timeout")));
							throw new CompletionException(new LoginTimeoutException(timeoutSeconds));
						})
						.thenCompose(loggedUser -> CasLogin.get().getLoginDatabase()
								.put(player.getUniqueId(), loggedUser)
								.thenAccept(ignored -> {
									CasLogin.get().getLogger().info(String.format("Player '%s' logged as '%s'.", player.getUsername(), loggedUser.getUser().getLogin()));
									loggingPlayer.remove(player);
								})
								.thenApply(unused -> loggedUser)
						));
	}
}
