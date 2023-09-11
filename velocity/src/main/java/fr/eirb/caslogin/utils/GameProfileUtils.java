package fr.eirb.caslogin.utils;

import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.api.util.UuidUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.UUID;

public final class GameProfileUtils {
	private static final Field nameField;
	private static final Field idField;
	private static final Field undashedIdField;

	static {
		try {
			nameField = GameProfile.class.getDeclaredField("name");
			idField = GameProfile.class.getDeclaredField("id");
			undashedIdField = GameProfile.class.getDeclaredField("undashedId");
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static GameProfile cloneGameProfile(GameProfile prof){
		return new GameProfile(prof.getId(), prof.getName(), Collections.emptyList());
	}

	public static void setName(GameProfile prof, String newName){
		try {
			nameField.setAccessible(true);
			nameField.set(prof, newName);
			nameField.setAccessible(false);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setUUID(GameProfile prof, UUID id){
		try {
			idField.setAccessible(true);
			undashedIdField.setAccessible(true);
			idField.set(prof, id);
			undashedIdField.set(prof, UuidUtils.toUndashed(id));
			idField.setAccessible(false);
			undashedIdField.setAccessible(false);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setToGameProfile(GameProfile prof, GameProfile ref){
		setName(prof, ref.getName());
		setUUID(prof, ref.getId());
	}
}
