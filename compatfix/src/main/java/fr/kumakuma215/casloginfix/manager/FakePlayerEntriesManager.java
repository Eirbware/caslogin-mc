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

	private final HashMap<UUID, FakePlayer> playerToFakePlayer;

	public FakePlayerEntriesManager() {
		playerToFakePlayer = new HashMap<>();
	}

	public void registerPlayer(UUID trueUUID, UUID falseUUID, String textureValue, String textureSignature) {
		playerToFakePlayer.put(falseUUID, new FakePlayer(trueUUID, SkinManager.VALUE, SkinManager.SIGNATURE));
		sendFakePlayerEntry(Objects.requireNonNull(CasLoginFix.INSTANCE.getServer().getPlayer(falseUUID)));
	}

	private PlayerInfoData createFakeInfoData(Player player) {
		return createFakeInfoData(player, player.getGameMode());
	}

	private PlayerInfoData createFakeInfoData(Player player, GameMode newGamemode) {
		if (!playerToFakePlayer.containsKey(player.getUniqueId()))
			throw new RuntimeException("DIDNT CHECK PLAYER CONTAIN BEFORE CREATEINFODATA");
		FakePlayer fakePlayer = playerToFakePlayer.get(player.getUniqueId());
		WrappedGameProfile profile = new WrappedGameProfile(fakePlayer.uuid(), player.getName());
		profile.getProperties().put("textures", new WrappedSignedProperty("textures", fakePlayer.textureValue(), fakePlayer.textureSignature()));
		return new PlayerInfoData(
				fakePlayer.uuid(),
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

	private void sendFakePlayerInfoPacket(Player player, PlayerInfoData data, EnumSet<EnumWrappers.PlayerInfoAction> actions){
		PacketContainer packetToSend = CasLoginFix.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
		packetToSend.getPlayerInfoActions().write(0, actions);
		packetToSend.getPlayerInfoDataLists().write(1, Collections.singletonList(data));
		CasLoginFix.getProtocolManager().sendServerPacket(player, packetToSend);


	}

	public void updateGamemode(Player player, GameMode newGamemode) throws NoFakePlayerException {
		if (!playerToFakePlayer.containsKey(player.getUniqueId()))
			throw new NoFakePlayerException();
		PlayerInfoData data = createFakeInfoData(player, newGamemode);
		sendFakePlayerInfoPacket(player, data, EnumSet.of(EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE));
	}

	public HashMap<UUID, FakePlayer> getPlayerToFakePlayer() {
		return playerToFakePlayer;
	}
}
