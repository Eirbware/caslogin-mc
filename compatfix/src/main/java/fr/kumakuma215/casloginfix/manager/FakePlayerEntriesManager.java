package fr.kumakuma215.casloginfix.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import fr.kumakuma215.casloginfix.CasLoginFix;
import fr.kumakuma215.casloginfix.FakePlayer;
import fr.kumakuma215.casloginfix.exceptions.NoFakePlayerException;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.*;

public class FakePlayerEntriesManager {

	private final HashMap<UUID, FakePlayer> falseUUIDToFakePlayer;

	public FakePlayerEntriesManager() {
		falseUUIDToFakePlayer = new HashMap<>();
	}

	public void registerPlayer(FakePlayer fakePlayer, UUID falseUUID) {
		falseUUIDToFakePlayer.put(falseUUID, fakePlayer);
		sendFakePlayerEntry(Objects.requireNonNull(CasLoginFix.INSTANCE.getServer().getPlayer(falseUUID)));
	}

	private PlayerInfoData createFakeInfoData(Player player) {
		return createFakeInfoData(player, player.getGameMode());
	}

	private PlayerInfoData createFakeInfoData(Player player, GameMode newGamemode) {
		if (!falseUUIDToFakePlayer.containsKey(player.getUniqueId()))
			throw new RuntimeException("DIDNT CHECK PLAYER CONTAIN BEFORE CREATEINFODATA");
		FakePlayer fakePlayer = falseUUIDToFakePlayer.get(player.getUniqueId());
		UUID trueUUID = fakePlayer.trueUUID();
		WrappedGameProfile profile = WrappedGameProfile.fromPlayer(player);
		if (fakePlayer.texture() != null) {
			profile.getProperties().clear();
			profile.getProperties().put(FakePlayer.TEXTURE_PROPERTY_NAME, fakePlayer.texture());
		}
		return new PlayerInfoData(
				trueUUID,
				0,
				false,
				EnumWrappers.NativeGameMode.fromBukkit(newGamemode),
				profile,
				WrappedChatComponent.fromText(player.getName())
		);
	}

	private void sendFakePlayerEntry(Player player) {
		PlayerInfoData data = createFakeInfoData(player);
		sendFakePlayerInfoPacket(player, data, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER, EnumWrappers.PlayerInfoAction.UPDATE_LISTED, EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE));
	}

	private void sendFakePlayerInfoPacket(Player player, PlayerInfoData data, EnumSet<EnumWrappers.PlayerInfoAction> actions) {
		PacketContainer packetToSend = CasLoginFix.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
		packetToSend.getPlayerInfoActions().write(0, actions);
		packetToSend.getPlayerInfoDataLists().write(1, Collections.singletonList(data));
		CasLoginFix.getProtocolManager().sendServerPacket(player, packetToSend);
	}

	public void updateGamemode(Player player, GameMode newGamemode) throws NoFakePlayerException {
		if (!falseUUIDToFakePlayer.containsKey(player.getUniqueId()))
			throw new NoFakePlayerException();
		PlayerInfoData data = createFakeInfoData(player, newGamemode);
		sendFakePlayerInfoPacket(player, data, EnumSet.of(EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE));
	}

	public void refreshSkin(Player player) throws NoFakePlayerException {
		if (!falseUUIDToFakePlayer.containsKey(player.getUniqueId()))
			throw new NoFakePlayerException();
		removeFakePlayerEntry(player);
		sendFakePlayerEntry(player);
	}

	public void refreshSkin(UUID uuid) throws NoFakePlayerException {
		if (!falseUUIDToFakePlayer.containsKey(uuid))
			throw new NoFakePlayerException();
		Player player = CasLoginFix.INSTANCE.getServer().getPlayer(uuid);
		assert player != null;
		removeFakePlayerEntry(player);
		sendFakePlayerEntry(player);
	}

	private void removeFakePlayerEntry(Player player) {
		PacketContainer packet = CasLoginFix.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
		packet.getUUIDLists().write(0, Collections.singletonList(falseUUIDToFakePlayer.get(player.getUniqueId()).trueUUID()));
		CasLoginFix.getProtocolManager().sendServerPacket(player, packet);
	}

	public FakePlayer getFakePlayer(Player p) {
		return falseUUIDToFakePlayer.get(p.getUniqueId());
	}
}
