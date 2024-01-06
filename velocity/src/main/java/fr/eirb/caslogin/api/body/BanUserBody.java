package fr.eirb.caslogin.api.body;

import fr.eirb.caslogin.model.CasUser;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.Date;

public class BanUserBody {
	final String banner;
	final String banned;
	final String reason;
	final long timestamp;
	final Long expires;

	public BanUserBody(@Nullable CasUser banner, CasUser banned, @Nullable String reason, @Nullable Duration banDuration) {
		this.banner = banner == null ? null : banner.getLogin();
		this.banned = banned.getLogin();
		this.reason = reason;
		Date now = new Date();
		this.timestamp = now.getTime();
		this.expires = banDuration == null ? null : Timestamp.from(now.toInstant().plus(banDuration)).getTime();
	}
}
