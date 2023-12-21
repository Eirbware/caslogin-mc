package fr.eirb.caslogin.model;

import javax.annotation.Nullable;
import java.sql.Date;

public record Ban(String bannedUser, String banner, @Nullable String reason, Date timestamp, @Nullable Date expires) {
}
