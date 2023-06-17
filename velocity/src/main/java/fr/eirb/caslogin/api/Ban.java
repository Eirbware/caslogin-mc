package fr.eirb.caslogin.api;

import javax.annotation.Nullable;
import java.sql.Date;

public class Ban {
	public String bannedUser;
	public String banner;
	@Nullable
	public String reason;
	public Date timestamp;
	@Nullable
	public Date expires;

}
