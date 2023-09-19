package fr.eirb.common.compatfix;

import java.util.UUID;

public class CasFixMessage {
	private static final int MESSAGE_LENGTH = 2;
	private static final String FORMAT = "%s:".repeat(MESSAGE_LENGTH).substring(0, MESSAGE_LENGTH*3-1);
	private final UUID falseUUID;
	private final UUID trueUUID;

	public CasFixMessage(String buffer) {
		String[] split = buffer.split(":");
		if (split.length != MESSAGE_LENGTH)
			throw new RuntimeException("INVALID FORMAT FOR PLUGIN MESSAGE");
		trueUUID = UUID.fromString(split[0].trim());
		falseUUID = UUID.fromString(split[1].trim());
	}

	public CasFixMessage(UUID trueUUID, UUID falseUUID) {
		this.falseUUID = falseUUID;
		this.trueUUID = trueUUID;
	}

	public UUID getFalseUUID() {
		return falseUUID;
	}

	public UUID getTrueUUID() {
		return trueUUID;
	}

	@Override
	public String toString() {
		return String.format(FORMAT,
				trueUUID.toString(),
				falseUUID.toString());
	}
}
