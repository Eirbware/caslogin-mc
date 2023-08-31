package fr.eirb.caslogin.utils;

import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.api.util.UuidUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.UUID;

public final class GameProfileUtils {
	public static GameProfile cloneGameProfile(GameProfile prof){
		return new GameProfile(prof.getId(), prof.getName(), Collections.emptyList());
	}

	public static void setName(GameProfile prof, String newName){
		try {
			Field nameField = prof.getClass().getDeclaredField("name");
			nameField.setAccessible(true);
			nameField.set(prof, newName);
			nameField.setAccessible(false);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setUUID(GameProfile prof, UUID id){
		try {
			Field idField = prof.getClass().getDeclaredField("id");
			Field undashedIdField = prof.getClass().getDeclaredField("undashedId");
			idField.setAccessible(true);
			undashedIdField.setAccessible(true);
			idField.set(prof, id);
			undashedIdField.set(prof, UuidUtils.toUndashed(id));
			idField.setAccessible(false);
			undashedIdField.setAccessible(false);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setToGameProfile(GameProfile prof, GameProfile ref){
		setName(prof, ref.getName());
		setUUID(prof, ref.getId());
	}
}
