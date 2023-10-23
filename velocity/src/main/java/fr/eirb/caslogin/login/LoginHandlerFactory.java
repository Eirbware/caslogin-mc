package fr.eirb.caslogin.login;

public final class LoginHandlerFactory {
	public static LoginHandler getAPILoginHandler(){
		return new APILoginHandlerImpl();
	}
}
