package fr.kumakuma215.casloginfix.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import fr.kumakuma215.casloginfix.CasLoginFix;
import fr.kumakuma215.casloginfix.exceptions.NoFakePlayerException;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.*;

public class FakePlayerEntriesManager {
	private final HashMap<UUID, UUID> falseToTrueUUIDMap;

	public FakePlayerEntriesManager() {
		falseToTrueUUIDMap = new HashMap<>();
	}

	public void registerPlayer(UUID trueUUID, UUID falseUUID) {
		falseToTrueUUIDMap.put(falseUUID, trueUUID);
		sendFakePlayerEntry(Objects.requireNonNull(CasLoginFix.INSTANCE.getServer().getPlayer(falseUUID)));
	}

	private PlayerInfoData createFakeInfoData(Player player) {
		return createFakeInfoData(player, player.getGameMode());
	}

	private PlayerInfoData createFakeInfoData(Player player, GameMode newGamemode) {
		if (!falseToTrueUUIDMap.containsKey(player.getUniqueId()))
			throw new RuntimeException("DIDNT CHECK PLAYER CONTAIN BEFORE CREATEINFODATA");
		UUID trueUUID = falseToTrueUUIDMap.get(player.getUniqueId());
		return new PlayerInfoData(
				trueUUID,
				0,
				false,
				EnumWrappers.NativeGameMode.fromBukkit(newGamemode),
				new WrappedGameProfile(trueUUID, player.getName()),
				WrappedChatComponent.fromText(player.getName())
		);
	}

	private void sendFakePlayerEntry(Player player) {
		PlayerInfoData data = createFakeInfoData(player);
		sendFakePlayerInfoPacket(player, data, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER, EnumWrappers.PlayerInfoAction.UPDATE_LISTED, EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE));
	}

	private void sendFakePlayerInfoPacket(Player player, PlayerInfoData data, EnumSet<EnumWrappers.PlayerInfoAction> actions){
		PacketContainer packetToSend = CasLoginFix.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
		packetToSend.getPlayerInfoActions().write(0, actions);
		packetToSend.getPlayerInfoDataLists().write(1, Collections.singletonList(data));
		CasLoginFix.getProtocolManager().sendServerPacket(player, packetToSend);


	}

	public void updateGamemode(Player player, GameMode newGamemode) throws NoFakePlayerException {
		if(!falseToTrueUUIDMap.containsKey(player.getUniqueId()))
			throw new NoFakePlayerException();
		PlayerInfoData data = createFakeInfoData(player, newGamemode);
		sendFakePlayerInfoPacket(player, data, EnumSet.of(EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE));
	}
}
