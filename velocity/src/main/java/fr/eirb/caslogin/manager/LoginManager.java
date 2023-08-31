package fr.eirb.caslogin.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.velocitypowered.api.proxy.Player;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.api.LoggedUser;
import fr.eirb.caslogin.exceptions.api.APIException;
import fr.eirb.caslogin.exceptions.api.Errors;
import fr.eirb.caslogin.exceptions.login.*;
import fr.eirb.caslogin.utils.ApiUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class LoginManager {

	public static BiMap<UUID, LoggedUser> loggedUserMap = HashBiMap.create();

	private static Set<Player> loggingPlayer = new HashSet<>();

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

	public static CompletableFuture<LoggedUser> pollLogin(Player player, int timeoutSeconds, int intervalSeconds) {
		if(loggingPlayer.contains(player))
			return CompletableFuture.failedFuture(new AlreadyLoggingInException());
		loggingPlayer.add(player);
		return CompletableFuture.supplyAsync(() -> {
			int counter = 0;
			while(counter < timeoutSeconds){
				CasLogin.getINSTANCE().getLogger().info("TRYING TO CHECK FOR player " + player.getUsername());
				LoggedUser user;
				try {
					user = ApiUtils.getLoggedUser(player.getUniqueId());
					CasLogin.getINSTANCE().getLogger().info("Got '" + user + "'");

				} catch (APIException e) {
					CasLogin.getINSTANCE().getLogger().severe("API EXCEPTION???");
					loggingPlayer.remove(player);
					throw new IllegalStateException(e);
				}
				if(user != null) {
					CasLogin.getINSTANCE().getLogger().info("GOT USER " + user.getUser().getLogin());
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
			loggingPlayer.remove(player);
			return null;
		});
	}
}
