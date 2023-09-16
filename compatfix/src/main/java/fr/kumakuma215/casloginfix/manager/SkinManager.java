package fr.kumakuma215.casloginfix.manager;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.entity.Player;

public class SkinManager {
	public static final String TEXTURES_PROPERTY_NAME = "textures";

	public static void changeSkin(Player player, String value, String signature){
		PlayerProfile profile = player.getPlayerProfile();
		profile.getProperties().removeIf((p) -> p.getName().equals(TEXTURES_PROPERTY_NAME));
		profile.getProperties().add(new ProfileProperty(TEXTURES_PROPERTY_NAME, value, signature));
		player.setPlayerProfile(profile);
		refreshSkin(player);
	}

	private static void refreshSkin(Player player) {

	}
}
