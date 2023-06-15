package fr.eirb.caslogin;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;

import java.util.logging.Logger;

@Plugin(
		id = Constants.PLUGIN_ID,
		name = Constants.PLUGIN_NAME,
		version = Constants.VERSION

)
public class CasLogin {
	@Inject
	private Logger logger;


}
