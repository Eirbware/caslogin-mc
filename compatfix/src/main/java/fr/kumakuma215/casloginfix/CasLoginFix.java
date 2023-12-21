package fr.kumakuma215.casloginfix;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import fr.eirb.common.compatfix.Constants;
import fr.kumakuma215.casloginfix.listeners.PluginMessageListener;
import fr.kumakuma215.casloginfix.listeners.SkinApplyEventListener;
import fr.kumakuma215.casloginfix.listeners.UpdateFakePlayer;
import fr.kumakuma215.casloginfix.manager.FakePlayerEntriesManager;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.event.SkinApplyEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CasLoginFix extends JavaPlugin {

    private boolean hasSkinRestorer = false;

    public static CasLoginFix INSTANCE;

    private ProtocolManager protocolManager;

    private FakePlayerEntriesManager fakePlayerEntriesManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        fakePlayerEntriesManager = new FakePlayerEntriesManager();
        getLogger().info("Registering plugin channel caslogin:auth");
        getServer().getMessenger().registerIncomingPluginChannel(this, Constants.CAS_FIX_CHANNEL, new PluginMessageListener());
        getServer().getPluginManager().registerEvents(new UpdateFakePlayer(), this);
        hasSkinRestorer = getServer().getPluginManager().isPluginEnabled("SkinsRestorer");
        if(hasSkinRestorer){
            SkinsRestorerProvider.get().getEventBus().subscribe(this, SkinApplyEvent.class, new SkinApplyEventListener());
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ProtocolManager getProtocolManager() {
        return INSTANCE.protocolManager;
    }

    public static FakePlayerEntriesManager getFakePlayerEntriesManager() {
        return INSTANCE.fakePlayerEntriesManager;
    }

    public boolean hasSkinRestorer() {
        return hasSkinRestorer;
    }
}
