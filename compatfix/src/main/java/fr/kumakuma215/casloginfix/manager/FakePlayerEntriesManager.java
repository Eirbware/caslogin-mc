package fr.kumakuma215.casloginfix.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import fr.kumakuma215.casloginfix.CasLoginFix;
import org.bukkit.entity.Player;

import java.util.*;

public class FakePlayerEntriesManager {
	private final HashMap<UUID, UUID> trueToFalseUUIDMap;

	public FakePlayerEntriesManager(){
		trueToFalseUUIDMap = new HashMap<>();
	}

	public void registerPlayer(UUID trueUUID, UUID falseUUID){
		sendFakePlayerEntry(Objects.requireNonNull(CasLoginFix.INSTANCE.getServer().getPlayer(falseUUID)), trueUUID);
		trueToFalseUUIDMap.put(trueUUID, falseUUID);
	}

	private void sendFakePlayerEntry(Player player, UUID trueUUID) {
		PacketContainer packetToSend = CasLoginFix.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
		packetToSend.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER,
				EnumWrappers.PlayerInfoAction.UPDATE_LISTED, EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE));
		PlayerInfoData data = new PlayerInfoData(
				trueUUID,
				0,
				false,
				EnumWrappers.NativeGameMode.SPECTATOR,
				new WrappedGameProfile(trueUUID, "FAKE"),
				WrappedChatComponent.fromText("FAKE"));
		packetToSend.getPlayerInfoDataLists().write(1, Collections.singletonList(data));
		CasLoginFix.getProtocolManager().sendServerPacket(player, packetToSend);
	}
}
