package fr.eirb.caslogin.listeners;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import fr.eirb.caslogin.CasLogin;
import fr.eirb.caslogin.utils.MessagesEnum;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FreezePlayer implements Listener {

	@EventHandler
	public void onChat(AsyncChatEvent ev){
		Player player = ev.getPlayer();
		if(CasLogin.isNotLoggedIn(player)){
			ev.setCancelled(true);
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent ev){
		Player player = ev.getPlayer();
		if(CasLogin.isNotLoggedIn(player)){
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, -1, 255, false, false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, -1, 128, false, false));
			player.setCollidable(false);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent ev){
		if(ev.getEntity() instanceof Player player){
			if(CasLogin.isNotLoggedIn(player))
				ev.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamageEntity(EntityDamageByEntityEvent ev){
		if(ev.getEntity() instanceof Player player){
			if(CasLogin.isNotLoggedIn(player))
				ev.setCancelled(true);
		}
		if(ev.getDamager() instanceof Player player){
			if(CasLogin.isNotLoggedIn(player))
				ev.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent ev){
		if(CasLogin.isNotLoggedIn(ev.getPlayer()))
			ev.getPlayer().teleport(ev.getFrom());
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent ev){
		if(ev.getTarget() instanceof Player player){
			if(CasLogin.isNotLoggedIn(player)){
				ev.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onKick(PlayerKickEvent ev){
		if(ev.reason().equals(MiniMessage.miniMessage().deserialize(MessagesEnum.LOGIN_SUCCESS.str)))
			ev.leaveMessage(Component.empty());
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent ev){
		if(CasLogin.isNotLoggedIn(ev.getPlayer()))
			if(!ev.getMessage().startsWith("/login")) {
				ev.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(MessagesEnum.ASK_LOGIN.str));
				ev.setCancelled(true);
			}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent ev){
		if(CasLogin.isNotLoggedIn(ev.getPlayer()))
			ev.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent ev){
		if(CasLogin.isNotLoggedIn(ev.getPlayer()))
			ev.setCancelled(true);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent ev){
		if(CasLogin.isNotLoggedIn(ev.getPlayer()))
			ev.setCancelled(true);
	}

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent ev){
		if(CasLogin.isNotLoggedIn(ev.getPlayer()))
			ev.setCancelled(true);
	}

	@EventHandler
	public void onPlayerPickup(PlayerAttemptPickupItemEvent ev){
		if(CasLogin.isNotLoggedIn(ev.getPlayer()))
			ev.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerPickupExp(PlayerPickupExperienceEvent ev){
		if(CasLogin.isNotLoggedIn(ev.getPlayer()))
			ev.setCancelled(true);
	}


	@EventHandler
	public void onPlayerPickupArrow(PlayerPickupArrowEvent ev){
		if(CasLogin.isNotLoggedIn(ev.getPlayer()))
			ev.setCancelled(true);
	}
}
