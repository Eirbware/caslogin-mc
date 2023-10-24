package fr.kumakuma215.casloginfix;

import fr.eirb.common.compatfix.CasFixMessage;

import java.util.UUID;

public record FakePlayer(UUID trueUUID, UUID falseUUID, String trueName) {
	public FakePlayer(CasFixMessage message){
		this(message.getTrueUUID(), message.getFalseUUID(), message.getTrueName());
	}
}
