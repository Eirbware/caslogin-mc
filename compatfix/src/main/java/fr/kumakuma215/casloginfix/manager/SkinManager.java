package fr.kumakuma215.casloginfix.manager;

import fr.kumakuma215.casloginfix.CasLoginFix;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.storage.PlayerStorage;
import net.skinsrestorer.api.storage.SkinStorage;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SkinManager {
	public static final String TEXTURES_PROPERTY_NAME = "textures";

	public static String VALUE = "ewogICJ0aW1lc3RhbXAiIDogMTY5NTExMDY4ODQyMSwKICAicHJvZmlsZUlkIiA6ICJjNzA3NTVhYzk2YjY0NTNlOWUwMzIyOWVkZTdkMDgwZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJrdW1ha3VtYTIxNSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yODE1MTYzZGViNzVlYzM2OTU0ZThkZThjNzQxMTIwZTQ1YWQ2ZmQzODE0ZWRlN2M0MWYwODA0ZDY5NTI4MDdkIgogICAgfQogIH0KfQ==";
	public static String SIGNATURE = "COivAs9iysjRNYwy2QRmK0hYOO7vT/336TJA61gyB5nZi8ysgzGCAKxtdvRshnJxHJaoEV1a+FY8FfjZBavFXnwnCP6NXVB2tYOCFoX4SAWSGjPtl09gFRnbtZuSq8rgxS41NmbNs+KqX0KHatgAcDrwy6GWv7NI5/KTZQjpx7ViXiG1ab5LB6fVEXztlXYdkfjva1qPS+zfJV3uXuuvJbZsD+RcRyCG8naaUIZyoyh0QewI6U+d4GF/mHH5XI7PKWKcDrHxdzzV/jNhidHI0wqtrFG6blSyi5GTDoyXChMMTeiQfsRONLlyI3N2SpmE0kjMu8g81G1eIa7NZbnX1D1FpDJPVs2bvT4r3eeWsQHRZqpiJtmpUaTNepKc9uovzbiRLC75aQO0oe6HLHbtWwl+UwGxsZfu9muGRJKLat9iNsETOwn1+W8Ym3IbbBA8+rcc8Vw7m6+qbZ+IpFpGvjmW1054iPgsetFPDvxBL7r+ehp3bDv4+mJ+LYkbEgUfUunF08kwWDklVPkEw3ME/6dNK35s1Tf2V+X+Eo+nQq6p4bY0o2f/a8hEPB1oV0xzuL8iOf6r1kS9foAUL1BWYpXDRv2eOBatOBNClTG5R6KR0B9PPOw1Uy/KTTxUt8JdkNO6xvV6Bgls1ekOzhqlBBvo+kh0zTiDttB7soFbkKY=";

	public static void changeSkin(Player player, String value, String signature) {
		System.out.println(value);
		System.out.println(signature);
		SkinsRestorer api = SkinsRestorerProvider.get();
		SkinStorage skinStorage = api.getSkinStorage();
		String skinName = String.format("%s_custom", player.getName());
		skinStorage.setCustomSkinData(skinName, SkinProperty.of(VALUE, SIGNATURE));
		Optional<InputDataResult> result = skinStorage.findSkinData(skinName);
		if(result.isEmpty()){
			System.out.println("Baise");
			return;
		}
		PlayerStorage playerStorage = api.getPlayerStorage();
		playerStorage.setSkinIdOfPlayer(player.getUniqueId(), result.get().getIdentifier());
		playerStorage.setSkinIdOfPlayer(CasLoginFix.getFakePlayerEntriesManager().getPlayerToFakePlayer().get(player.getUniqueId()).uuid(), result.get().getIdentifier());
		try {
			api.getSkinApplier(Player.class).applySkin(player);
		} catch (DataRequestException e) {
			throw new RuntimeException(e);
		}

	}
}
