package fr.eirb.caslogin.proxy.connection;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.GameProfile;
import fr.eirb.caslogin.AsyncMap;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;

public interface GameProfileDatabase extends AsyncMap<Player, GameProfile> {
}
