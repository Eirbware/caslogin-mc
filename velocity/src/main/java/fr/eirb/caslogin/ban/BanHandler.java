package fr.eirb.caslogin.ban;

import fr.eirb.caslogin.model.Ban;
import fr.eirb.caslogin.model.CasUser;
import fr.eirb.caslogin.model.LoggedUser;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BanHandler {
	CompletableFuture<Void> banUser(@Nullable CasUser banner, CasUser toBan, @Nullable String reason, @Nullable Duration banDuration);
	CompletableFuture<Void> unbanUser(CasUser user);
}
