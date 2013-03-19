package me.limebyte.battlenight.core.listeners;

import me.limebyte.battlenight.api.BattleNightAPI;
import me.limebyte.battlenight.api.battle.Battle;
import me.limebyte.battlenight.core.battle.SimpleTeamedBattle;
import me.limebyte.battlenight.core.tosort.ConfigManager;
import me.limebyte.battlenight.core.tosort.ConfigManager.Config;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

public class HealthListener extends APIRelatedListener {

    public HealthListener(BattleNightAPI api) {
        super(api);
    }

    private boolean canBeDamaged(Player damaged, Battle battle, EntityDamageByEntityEvent event) {
        Entity eDamager = event.getDamager();
        Player damager;

        if (eDamager instanceof Projectile) {
            LivingEntity shooter = ((Projectile) eDamager).getShooter();
            if (shooter instanceof Player) {
                damager = (Player) shooter;
            } else return true;
        } else {
            if (eDamager instanceof Player) {
                damager = (Player) eDamager;
            } else return true;
        }

        if (getAPI().getSpectatorManager().getSpectators().contains(damager.getName())) return false;

        if (battle.containsPlayer(damager)) {
            if (!battle.isInProgress()) return false;
            if (damager == damaged) return true;

            if (battle instanceof SimpleTeamedBattle) {
                if (((SimpleTeamedBattle) battle).areEnemies(damager, damaged)) return true;
                return ConfigManager.get(Config.MAIN).getBoolean("FriendlyFire", false);
            }

            return true;
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        BattleNightAPI api = getAPI();

        if (api.getSpectatorManager().getSpectators().contains(player.getName())) {
            event.setCancelled(true);
            return;
        }

        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
            Battle battle = api.getBattle();

            if (!battle.containsPlayer(player)) return;
            subEvent.setCancelled(!canBeDamaged(player, battle, subEvent));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        if (!ConfigManager.get(Config.MAIN).getBoolean("StopHealthRegen", true)) return;
        if (!getAPI().getBattle().containsPlayer(player)) return;

        RegainReason reason = event.getRegainReason();
        if (reason == RegainReason.REGEN || reason == RegainReason.SATIATED) {
            event.setCancelled(true);
        }
    }

}
