package me.limebyte.battlenight.core.listeners;

import me.limebyte.battlenight.core.BattleNight;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    // Get Main Class
    public static BattleNight plugin;

    public DamageListener(BattleNight instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final Player player = (Player) event.getEntity();

        if (event instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;

            if (BattleNight.getBattle().spectators.contains(player.getName())) event.setCancelled(true);

            if (!BattleNight.getBattle().usersTeam.containsKey(player.getName())) return;

            subEvent.setCancelled(!canBeDamaged(player, subEvent.getDamager()));

        }

    }

    private boolean canBeDamaged(Player damaged, Entity eDamager) {
        Player damager;

        if (eDamager instanceof Projectile) {
            LivingEntity shooter = ((Projectile) eDamager).getShooter();
            if (shooter instanceof Player)
                damager = (Player) shooter;
            else return true;
        } else {
            if (eDamager instanceof Player) {
                damager = (Player) eDamager;
            }
            else {
                return true;
            }
        }

        if (BattleNight.getBattle().usersTeam.containsKey(damager.getName())) {
            if (BattleNight.playersInLounge)
                return false;
            if (areEnemies(damager, damaged) || damager == damaged) {
                return true;
            } else {
                return BattleNight.configFriendlyFire;
            }
        }

        return true;
    }

    private boolean areEnemies(Player player1, Player player2) {
        if (BattleNight.getBattle().usersTeam.get(player1.getName()) != BattleNight.getBattle().usersTeam.get(player2.getName())) {
            return true;
        } else {
            return false;
        }
    }

}
