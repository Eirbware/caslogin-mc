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
import fr.eirb.caslogin.exceptions.api.APIException;
import fr.eirb.caslogin.exceptions.api.Errors;
import fr.eirb.caslogin.exceptions.login.*;
import fr.eirb.caslogin.utils.ApiUtils;
import fr.eirb.caslogin.utils.GameProfileUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
		if(loggedUserMap.containsKey(player.getUniqueId()))
			return CompletableFuture.completedFuture(loggedUserMap.get(player.getUniqueId()));
		loggingPlayer.add(player);
		return CompletableFuture.supplyAsync(() -> {
			int counter = 0;
			while(counter < timeoutSeconds){
				LoggedUser user;
				try {
					user = ApiUtils.getLoggedUser(player.getUniqueId());

				} catch (APIException e) {
					CasLogin.getINSTANCE().getLogger().severe("API EXCEPTION???");
					loggingPlayer.remove(player);
					throw new IllegalStateException(e);
				}
				if(user != null) {
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
			loggingPlayer.remove(player);
			return null;
		});
	}

	public static Optional<LoggedUser> getLoggedPlayer(Player p){
		return Optional.ofNullable(loggedUserMap.get(p.getUniqueId()));
	}

	public static void moveLoggedPlayer(Player player, ProxyServer proxy, LoggedUser loggedUser){
		player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.login.success")));
		GameProfile prof = player.getGameProfile();
		GameProfile oldProf = GameProfileUtils.cloneGameProfile(prof);
		GameProfileUtils.setName(prof, loggedUser.getUser().getLogin());
		GameProfileUtils.setUUID(prof, loggedUser.getFakeUserUUID());
		RegisteredServer loggedServer = proxy.getServer(ConfigurationManager.getLoggedServer()).orElseThrow();
		player.createConnectionRequest(loggedServer).connect()
				.thenAccept((r) -> {
					GameProfileUtils.setToGameProfile(prof, oldProf);
					if (!r.isSuccessful()) {
						if (r.getReasonComponent().isEmpty())
							player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigurationManager.getLang("user.errors.user_disconnected_no_reason")));
						else
							player.sendMessage(MiniMessage
									.miniMessage()
									.deserialize(ConfigurationManager.getLang("user.errors.user_disconnected"))
									.append(r.getReasonComponent().get()));
						try {
							LoginManager.logout(player);
						} catch (NotLoggedInException ignored) {
						}
					} else {
						proxy.getEventManager().fireAndForget(new PostLoginEvent(player, loggedUser));
					}
				})
				.exceptionally((throwable) -> {
					GameProfileUtils.setToGameProfile(prof, oldProf);
					return null;
				});
	}

	public static void cacheLoggedUsers() {
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
