package fr.eirb.caslogin.exceptions.configuration;

import fr.eirb.caslogin.configuration.LoginHandlerTypes;

import java.util.Arrays;

public class NoSuchLoginHandler extends Exception{
	public NoSuchLoginHandler(String loginHandler) {
		super(String.format("There are no loginHandler of type %s. The available types are: %s", loginHandler, String.join(Arrays.toString(LoginHandlerTypes.values()))));
	}
}
