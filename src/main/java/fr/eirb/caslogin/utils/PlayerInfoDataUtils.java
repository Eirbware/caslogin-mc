package fr.eirb.caslogin.utils;

import com.comphenix.protocol.wrappers.PlayerInfoData;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class PlayerInfoDataUtils {
	public static PlayerInfoData changeNameOfPlayer(PlayerInfoData old, String newName){
		return new PlayerInfoData(
				old.getProfile().withName(newName),
				old.getLatency(),
				old.getGameMode(),
				old.getDisplayName());
	}

	public static UUID getUUIDOfPlayerNameOffline(String username){
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
	}
}
