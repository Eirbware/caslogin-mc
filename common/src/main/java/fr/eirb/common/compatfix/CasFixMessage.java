package fr.eirb.common.compatfix;

import java.util.UUID;

public class CasFixMessage {
	private final UUID falseUUID;
	private final UUID trueUUID;
	public CasFixMessage(String buffer){
		String[] split = buffer.split(":");
		if(split.length != 2)
			throw new RuntimeException("INVALID FORMAT FOR PLUGIN MESSAGE");
		trueUUID = UUID.fromString(split[0].trim());
		falseUUID = UUID.fromString(split[1].trim());
	}

	public CasFixMessage(UUID trueUUID, UUID falseUUID){
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
		return String.format("%s:%s", trueUUID.toString(), falseUUID.toString());
	}
}
