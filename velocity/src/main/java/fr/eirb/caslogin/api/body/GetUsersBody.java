package fr.eirb.caslogin.api.body;

import fr.eirb.caslogin.model.LoggedUser;

import java.util.List;

public class GetUsersBody {
	private List<LoggedUser> users;

	public List<LoggedUser> getUsers() {
		return users;
	}
}
