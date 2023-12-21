package fr.eirb.caslogin.login;

import fr.eirb.caslogin.api.handlers.APILoginHandlerImpl;

public final class LoginHandlerFactory {
	public static LoginHandler getAPILoginHandler(){
		return new APILoginHandlerImpl();
	}
}
