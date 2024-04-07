package fr.eirb.common.compatfix;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CasFixMessage {
	private static final int MESSAGE_LENGTH = 4;
	private static final String FORMAT = "%s:".repeat(MESSAGE_LENGTH).substring(0, MESSAGE_LENGTH * 3 - 1);
	private final UUID falseUUID;
	private final UUID trueUUID;
	private final String trueName;
	private final String diplome;

	public CasFixMessage(String buffer) {
		String[] split = buffer.split(":");
		if (split.length != MESSAGE_LENGTH)
			throw new RuntimeException("INVALID FORMAT FOR PLUGIN MESSAGE");
		trueUUID = UUID.fromString(split[0].trim());
		falseUUID = UUID.fromString(split[1].trim());
		this.trueName = split[2].trim();
		this.diplome = split[3].trim();
	}

	public CasFixMessage(UUID trueUUID, UUID falseUUID, String trueName, String diplome) {
		this.falseUUID = falseUUID;
		this.trueUUID = trueUUID;
		this.trueName = trueName;
		this.diplome = diplome;
	}

	public UUID getFalseUUID() {
		return falseUUID;
	}

	public UUID getTrueUUID() {
		return trueUUID;
	}

	public String getTrueName() {
		return trueName;
	}

	public String getDiplome() {
		return diplome;
	}

	@Override
	public String toString() {
		return String.format(FORMAT,
				trueUUID.toString(),
				falseUUID.toString(),
				trueName,
				diplome);
	}

	public byte[] toByteArray() {
		return StandardCharsets.UTF_8.encode(this.toString()).array();
	}

}
