package fr.eirb.common.compatfix;

import java.util.UUID;

public class CasFixMessage {
	private final UUID falseUUID;
	private final UUID trueUUID;
	private final String textureValue;
	private final String textureSignature;

	public CasFixMessage(String buffer) {
		String[] split = buffer.split(":");
		if (split.length != 4)
			throw new RuntimeException("INVALID FORMAT FOR PLUGIN MESSAGE");
		trueUUID = UUID.fromString(split[0].trim());
		falseUUID = UUID.fromString(split[1].trim());
		textureValue = split[2];
		textureSignature = split[3];
	}

	public CasFixMessage(UUID trueUUID, UUID falseUUID, String texture, String signature) {
		this.falseUUID = falseUUID;
		this.trueUUID = trueUUID;
		this.textureValue = texture;
		this.textureSignature = signature;
	}

	public UUID getFalseUUID() {
		return falseUUID;
	}

	public UUID getTrueUUID() {
		return trueUUID;
	}

	@Override
	public String toString() {
		return String.format("%s:%s:%s:%s",
				trueUUID.toString(),
				falseUUID.toString(),
				textureValue,
				textureSignature);
	}

	public String getTextureValue() {
		return textureValue;
	}

	public String getTextureSignature() {
		return textureSignature;
	}
}
