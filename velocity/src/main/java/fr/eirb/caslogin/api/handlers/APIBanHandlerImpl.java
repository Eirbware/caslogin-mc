package fr.eirb.caslogin.api.handlers;

import fr.eirb.caslogin.ban.BanHandler;
import fr.eirb.caslogin.exceptions.api.APIException;
import fr.eirb.caslogin.model.Ban;
import fr.eirb.caslogin.model.CasUser;
import fr.eirb.caslogin.model.LoggedUser;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class APIBanHandlerImpl implements BanHandler {
	@Override
	public CompletableFuture<Void> banUser(@Nullable CasUser banner, CasUser toBan, @Nullable String reason, @Nullable Duration banDuration) {
		return CompletableFuture.runAsync(() -> {
			try {
				ApiUtils.banUser(banner, toBan, reason, banDuration);
			} catch (APIException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Void> unbanUser(CasUser user) {
		return null;
	}
}
