package fr.eirb.caslogin.utils;

public enum MessagesEnum {
	LOGIN_SUCCESS("<green>You have been successfully logged in</green><br><gray>Please log back in</gray>"),

	LOGOUT_SUCCESS("<green>You have been successfully logged out</green>"),

	USER_NOT_LOGGED_IN("<red>This user is not logged in!</red>"),

	NOT_LOGGED_IN("<red>You are not logged in!</red>"),

	ALREADY_LOGGED_IN("<red>You are already logged in!</red>"),

	LOGIN_TAKEN("<red>This user is already logged in!</red>"),

	FORCE_LOGGED_OUT("<red>You have been logged out by an admin.</red>"),

	LOGOUT_PLAYER("<green>You have successfully logged out the player</green> <gold><user></gold>"),

	ASK_LOGIN("<red>Veuillez vous connecter avec la commande</red> <gold>/cas login</gold>"),

	NOT_ENOUGH_PERMISSION("<red>Not enough permission</red>"),

	BANNED("<red>You have been banned by an administrator.</red>"),

	BAN_USER("<green>You have successfully banned the user</green> <gold><user></gold> <green>!</green>"),

	UNBAN_USER("<green>You have successfully unbanned the user</green> <gold><user></gold> <green>!</green>"),

	ALREADY_BANNED("<red>The user</red> <gold><user></gold> <red>is already banned.</red>"),

	NOT_BANNED("<red>The user</red> <gold><user></gold> <red>is not banned.</red>"),

	NOT_AN_ADMIN("<red>The user</red> <gold><user></gold> <red>is not an administrator.</red>"),

	ALREADY_ADMIN("<red>The user</red> <gold><user></gold> <red>is already an administrator.</red>"),

	ADD_ADMIN_SUCCESS("<green>You successfully added</green> <gold><user></gold> <green>to the administrators' list!</green>"),

	REMOVE_ADMIN_SUCCESS("<green>You successfully removed</green> <gold><user></gold> <green>from the administrators' list!</green>"),

	PLAYER_EXCLUSIVE_COMMAND("<red>This command can only be executed by a player...</red>"),
	;

	public final String str;

	MessagesEnum(String miniMessageRepr) {
		this.str = miniMessageRepr;
	}
}


