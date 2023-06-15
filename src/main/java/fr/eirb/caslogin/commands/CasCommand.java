package fr.eirb.caslogin.commands;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.eirb.caslogin.exceptions.configuration.AlreadyAdminException;
import fr.eirb.caslogin.exceptions.configuration.NotAdminException;
import fr.eirb.caslogin.exceptions.login.*;
import fr.eirb.caslogin.manager.ConfigurationManager;
import fr.eirb.caslogin.manager.LoginManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;

public final class CasCommand {
	public static BrigadierCommand createCasCommand(final ProxyServer proxy){
		return null;
	}
}
