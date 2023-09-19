package fr.kumakuma215.casloginfix;

import java.util.Objects;
import java.util.UUID;

public final class FakePlayer {
	private final UUID uuid;

	private String textureValue;
	private String textureSignature;

	public FakePlayer(UUID uuid, String textureValue, String textureSignature) {
		this.uuid = uuid;
		this.textureValue = textureValue;
		this.textureSignature = textureSignature;
	}

	public UUID uuid() {
		return uuid;
	}

	public String textureValue() {
		return textureValue;
	}

	public String textureSignature() {
		return textureSignature;
	}

	public void setTextureValue(String textureValue) {
		this.textureValue = textureValue;
	}

	public void setTextureSignature(String textureSignature) {
		this.textureSignature = textureSignature;
	}


}
