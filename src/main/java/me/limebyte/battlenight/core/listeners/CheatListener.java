package me.limebyte.battlenight.core.listeners;

import java.util.List;

import me.limebyte.battlenight.api.BattleNightAPI;
import me.limebyte.battlenight.api.battle.Battle;
import me.limebyte.battlenight.api.battle.Lobby;
import me.limebyte.battlenight.api.util.Message;
import me.limebyte.battlenight.core.tosort.ConfigManager;
import me.limebyte.battlenight.core.tosort.ConfigManager.Config;
import me.limebyte.battlenight.core.tosort.SafeTeleporter;
import me.limebyte.battlenight.core.util.BattlePlayer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class CheatListener extends APIRelatedListener {

    public CheatListener(BattleNightAPI api) {
        super(api);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Lobby lobby = getAPI().getLobby();

        BattlePlayer bPlayer = BattlePlayer.get(player.getName());
        if (!bPlayer.isAlive()) event.setCancelled(true);

        if (lobby.contains(player)) {
            event.setCancelled(true);
            getAPI().getMessenger().tell(player, Message.NO_CHEATING);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Lobby lobby = getAPI().getLobby();
        Battle battle = getAPI().getBattleManager().getBattle();

        BattlePlayer bPlayer = BattlePlayer.get(player.getName());
        if (lobby.contains(player) || !bPlayer.isAlive()) event.setCancelled(true);

        if (battle.containsPlayer(player) && event.getSlotType() == SlotType.OUTSIDE) {
            ItemStack cursor = event.getCursor();
            if (cursor != null && cursor.getType() != Material.AIR) {
                event.setCancelled(true);
                getAPI().getMessenger().tell(player, Message.NO_CHEATING);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Battle battle = getAPI().getBattleManager().getBattle();

        if (!getAPI().getLobby().contains(player) && !battle.containsPlayer(player)) return;
        if (!ConfigManager.get(Config.MAIN).getBoolean("Commands.Block", true)) return;

        List<String> whitelist = ConfigManager.get(Config.MAIN).getStringList("Commands.Whitelist");
        whitelist.add("bn");

        String[] cmdArg = event.getMessage().split(" ");
        String cmdString = cmdArg[0].trim().substring(1).toLowerCase();

        Command command = Bukkit.getServer().getPluginCommand(cmdString);
        if (command != null) {
            // Check if the command is listed
            if (whitelist.contains(command.getLabel().toLowerCase())) return;

            // Check if there are any aliases listed
            if (!command.getAliases().isEmpty()) {
                for (String alias : command.getAliases()) {
                    if (whitelist.contains(alias.toLowerCase())) return;
                }
            }
        } else {
            // Check if the command is listed
            if (whitelist.contains(cmdString.toLowerCase())) return;
        }

        // Its not listed so block it
        event.setCancelled(true);
        getAPI().getMessenger().tell(player, Message.NO_COMMAND);
        return;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Lobby lobby = getAPI().getLobby();
        Battle battle = getAPI().getBattleManager().getBattle();
        BattlePlayer bPlayer = BattlePlayer.get(player.getName());

        if (!bPlayer.isAlive() || battle.containsPlayer(player) || lobby.contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Lobby lobby = getAPI().getLobby();
        Battle battle = getAPI().getBattleManager().getBattle();

        if (!SafeTeleporter.telePass.contains(player.getName())) {
            if (lobby.contains(player)) {
                event.setCancelled(true);
                return;
            }

            if (battle.containsPlayer(player)) {
                switch (event.getCause()) {
                    case COMMAND:
                        if (!ConfigManager.get(Config.MAIN).getBoolean("Teleportation.Commands", false)) {
                            event.setCancelled(true);
                            getAPI().getMessenger().tell(player, Message.NO_TELEPORTING);
                        }
                        break;
                    case PLUGIN:
                        if (!ConfigManager.get(Config.MAIN).getBoolean("Teleportation.Plugins", false)) {
                            event.setCancelled(true);
                            getAPI().getMessenger().tell(player, Message.NO_TELEPORTING);
                        }
                        break;
                    case ENDER_PEARL:
                        if (!ConfigManager.get(Config.MAIN).getBoolean("Teleportation.EnderPearls", true)) {
                            event.setCancelled(true);
                            getAPI().getMessenger().tell(player, Message.NO_TELEPORTING);
                        }
                        break;
                    case NETHER_PORTAL:
                    case END_PORTAL:
                        if (!ConfigManager.get(Config.MAIN).getBoolean("Teleportation.Portals", true)) {
                            event.setCancelled(true);
                            getAPI().getMessenger().tell(player, Message.NO_TELEPORTING);
                        }
                        break;
                    case UNKNOWN:
                        if (!ConfigManager.get(Config.MAIN).getBoolean("Teleportation.Unknown", true)) {
                            event.setCancelled(true);
                            getAPI().getMessenger().tell(player, Message.NO_TELEPORTING);
                        }
                        break;
                    default:
                        event.setCancelled(true);
                        break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Lobby lobby = getAPI().getLobby();
        Projectile projectile = event.getEntity();

        if (projectile.getShooter() instanceof Player) {
            Player thrower = (Player) projectile.getShooter();

            BattlePlayer bPlayer = BattlePlayer.get(thrower.getName());
            if (!bPlayer.isAlive()) event.setCancelled(true);

            if (lobby.contains(thrower)) {
                event.setCancelled(true);
            }
        }
    }
}
