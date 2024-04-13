package fr.kumakuma215.casloginfix;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import fr.eirb.common.compatfix.CasFixMessage;
import fr.kumakuma215.casloginfix.utils.CasUtils;

import java.util.Objects;
import java.util.UUID;

public final class FakePlayer {
	public static final String TEXTURE_PROPERTY_NAME = "textures";
	private final UUID trueUUID;
	private final UUID falseUUID;
	private final String trueName;
	private final String diplome;
	private WrappedSignedProperty texture;

	public FakePlayer(UUID trueUUID, UUID falseUUID, String trueName, String diplome) {
		this.trueUUID = trueUUID;
		this.falseUUID = falseUUID;
		this.trueName = trueName;
		this.diplome = diplome;
	}

	public FakePlayer(CasFixMessage message) {
		this(message.getTrueUUID(), message.getFalseUUID(), message.getTrueName(), message.getDiplome());
	}

	public UUID trueUUID() {
		return trueUUID;
	}

	public UUID falseUUID() {
		return falseUUID;
	}

	public String trueName() {
		return trueName;
	}

	public String diplome() {
		return diplome;
	}

	public String getAccessory(){
		return CasUtils.getAccessoryFromDiploma(diplome);
	}

	public WrappedSignedProperty texture() {
		return texture;
	}

	public void setTexture(String value, String signature){
		this.texture = new WrappedSignedProperty(TEXTURE_PROPERTY_NAME, value, signature);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (FakePlayer) obj;
		return Objects.equals(this.trueUUID, that.trueUUID) &&
				Objects.equals(this.falseUUID, that.falseUUID) &&
				Objects.equals(this.trueName, that.trueName) &&
				Objects.equals(this.diplome, that.diplome) &&
				Objects.equals(this.texture, that.texture);
	}

	@Override
	public int hashCode() {
		return Objects.hash(trueUUID, falseUUID, trueName, diplome, texture);
	}

	@Override
	public String toString() {
		return "FakePlayer[" +
				"trueUUID=" + trueUUID + ", " +
				"falseUUID=" + falseUUID + ", " +
				"trueName=" + trueName + ", " +
				"diplome=" + diplome + ", " +
				"texture=" + texture + ']';
	}

}
