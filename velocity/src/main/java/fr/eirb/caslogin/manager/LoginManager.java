package fr.eirb.caslogin.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.util.GameProfile;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.api.LoggedUser;
import fr.eirb.caslogin.events.PostLoginEvent;
import fr.eirb.caslogin.events.PostLogoutEvent;
import fr.eirb.caslogin.exceptions.api.APIException;
import fr.eirb.caslogin.exceptions.api.Errors;
import fr.eirb.caslogin.exceptions.login.*;
import fr.eirb.caslogin.handlers.ChangeGameProfileHandler;
import fr.eirb.caslogin.utils.ApiUtils;
import fr.eirb.caslogin.utils.GameProfileUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class LoginManager {

	private static final BiMap<UUID, @NotNull  LoggedUser> loggedUserMap = HashBiMap.create();

	private static final Set<Player> loggingPlayer = new HashSet<>();

//	public static LoggedUser logPlayer(Player p, String authCode) throws LoginAlreadyTakenException,
//			InvalidAuthCodeException, AuthCodeExpiredException, InvalidTokenException, NoAuthCodeForUuidException {
//		try {
//			LoggedUser user = ApiUtils.validateLogin(p, authCode);
//			loggedUserMap.put(p.getUniqueId(), user);
//			return user;
//		} catch (APIException ex) {
//			switch (ex.error) {
//				case USER_ALREADY_LOGGED_IN -> throw new LoginAlreadyTakenException();
//				case INVALID_AUTH_CODE -> throw new InvalidAuthCodeException(authCode, p);
//				case AUTH_CODE_EXPIRED -> throw new AuthCodeExpiredException(authCode, p);
//				case INVALID_TOKEN -> throw new InvalidTokenException();
//				case NO_AUTH_CODE_FOR_UUID -> throw new NoAuthCodeForUuidException();
//				default -> throw new RuntimeException(ex);
//			}
//		}
//	}

	public static void logout(Player p) throws NotLoggedInException {
		CasLogin.getINSTANCE().getLogger().info(String.format("Trying to logout player '%s'", p.getUsername()));
		if(!loggedUserMap.containsKey(p.getUniqueId())) {
			CasLogin.getINSTANCE().getLogger().info(String.format("Player '%s' is not logged in.", p.getUsername()));
			throw new NotLoggedInException(p);
		}
		try{
			CasLogin.getINSTANCE().getLogger().info(String.format("Logging out player '%s'", p.getUsername()));
			LoggedUser userToLogOut = loggedUserMap.get(p.getUniqueId());
			loggedUserMap.remove(p.getUniqueId());
			ApiUtils.logout(userToLogOut);
			CasLogin.getINSTANCE().getProxy().getEventManager().fire(new PostLogoutEvent(p));
		}catch(APIException ex){
			CasLogin.getINSTANCE().getLogger().warning(String.format("Got exception '%s'", ex.getClass().getName()));
			if(ex.error == Errors.USER_NOT_LOGGED_IN)
				throw new NotLoggedInException(p);
			throw new RuntimeException(ex);
		}
	}

	public static Set<LoggedUser> getLoggedUsers(){
		return loggedUserMap.values();
	}

	public static CompletableFuture<LoggedUser> pollLogin(Player player, int timeoutSeconds, int intervalSeconds) {
		if(loggingPlayer.contains(player))
			return CompletableFuture.failedFuture(new AlreadyLoggingInException());
		if(loggedUserMap.containsKey(player.getUniqueId()))
			return CompletableFuture.completedFuture(loggedUserMap.get(player.getUniqueId()));
		loggingPlayer.add(player);
		CasLogin.getINSTANCE().getLogger().info(String.format("Starting logging poll for player '%s'", player.getUsername()));
		return CompletableFuture.supplyAsync(() -> {
			int counter = 0;
			while(counter < timeoutSeconds){
				LoggedUser user;
				try {
					user = ApiUtils.getLoggedUser(player.getUniqueId());

				} catch (APIException e) {
					CasLogin.getINSTANCE().getLogger().severe("API EXCEPTION ON LOGIN POLL! Something is really wrong.");
					loggingPlayer.remove(player);
					throw new IllegalStateException(e);
				}
				if(user != null) {
					CasLogin.getINSTANCE().getLogger().info(String.format("Player '%s' logged as '%s'.", player.getUsername(), user.getUser().getLogin()));
					loggedUserMap.put(player.getUniqueId(), user);
					loggingPlayer.remove(player);
					return user;
				}
				try {
					TimeUnit.SECONDS.sleep(intervalSeconds);
				} catch (InterruptedException e) {
					throw new IllegalStateException(e);
				}
				counter += intervalSeconds;
			}
			CasLogin.getINSTANCE().getLogger().info(String.format("Polling timed out for player '%s'", player.getUsername()));
			loggingPlayer.remove(player);
			player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("errors.login_timeout")));
			return null;
		});
	}

	public static Optional<LoggedUser> getLoggedPlayer(Player p){
		return Optional.ofNullable(loggedUserMap.get(p.getUniqueId()));
	}

	public static void moveLoggedPlayer(Player player, ProxyServer proxy, LoggedUser loggedUser){
		player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.login.success")));
		RegisteredServer loggedServer = CasLogin.getLoggedEntrypointServer();
		player.createConnectionRequest(loggedServer).connect()
				.thenAccept((r) -> {
					if (!r.isSuccessful()) {
						CasLogin.getINSTANCE().getLogger().info("Player got disconnected...");
						if (r.getReasonComponent().isEmpty())
							player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.user_disconnected_no_reason")));
						else
							player.sendMessage(MiniMessage
									.miniMessage()
									.deserialize(ConfigurationManager.getLang("user.errors.user_disconnected"))
									.append(r.getReasonComponent().get()));
						try {
							ChangeGameProfileHandler.getINSTANCE().restoreGameProfile(player);
							LoginManager.logout(player);
						} catch (NotLoggedInException ignored) {
						}
					} else {
						proxy.getEventManager().fireAndForget(new PostLoginEvent(player, loggedUser));
					}
				});
	}

	public static void resetLoggedUsers() {
		loggedUserMap.clear();
		try{
			List<LoggedUser> users = ApiUtils.getLoggedUsers();
			for(var user : users){
				loggedUserMap.put(user.getUuid(), user);
			}
		} catch (APIException e) {
			throw new IllegalStateException(e);
		}
	}
}
