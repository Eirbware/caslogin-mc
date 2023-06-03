package fr.eirb.caslogin.utils;

public enum MessagesEnum {
	LOGIN_SUCCESS("<green>You have been successfully logged in</green><br><gray>Please log back in</gray>"),

	LOGOUT_SUCCESS("<green>You have been successfully logged out</green>"),

	NOT_LOGGED_IN("<red>You are not logged in!</red>"),

	ALREADY_LOGGED_IN("<red>You are already logged in!</red>"),

	LOGIN_TAKEN("<red>This user is already logged in!</red>"),

	FORCE_LOGGED_OUT("<red>You have been logged out by an admin.</red>"),

	LOGOUT_PLAYER("<green>You have successfully logged out the player</green> <gold><user></gold>"),

	ASK_LOGIN("<red>Veuillez vous connecter avec la commande</red> <gold>/cas login</gold>"),

	PLAYER_EXCLUSIVE_COMMAND("<red>This command can only be executed by a player...</red>"),
	;

	public final String str;

	MessagesEnum(String miniMessageRepr) {
		this.str = miniMessageRepr;
	}
}


