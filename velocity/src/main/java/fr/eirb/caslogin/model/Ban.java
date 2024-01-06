package fr.eirb.caslogin.model;

import javax.annotation.Nullable;
import java.sql.Date;

public record Ban(CasUser bannedUser, CasUser banner, @Nullable String reason, Date timestamp, @Nullable Date expires) {
}
