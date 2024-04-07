package fr.eirb.caslogin.api.body;

import fr.eirb.caslogin.model.Role;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GetUsersBody {
	public record CompositeUser(String login, String ecole, String diplome, Role[] roles, @Nullable String uuid){};
	private List<CompositeUser> users;

	public List<CompositeUser> getCompositeUsers() {
		return users;
	}
}
