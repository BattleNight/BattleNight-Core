package me.limebyte.battlenight.core.listeners;

import me.limebyte.battlenight.core.BattleNight;
import me.limebyte.battlenight.core.battle.Team;
import me.limebyte.battlenight.core.util.Configuration;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ReadyListener implements Listener {

    // Get Main Class
    public static BattleNight plugin;

    public ReadyListener(BattleNight instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            final Block block = event.getClickedBlock();
            final Player player = event.getPlayer();
            final String name = player.getName();
            if ((block.getTypeId() == Configuration.config.getInt("ReadyBlock", 42))
                    && (BattleNight.getBattle().usersTeam.containsKey(name) && (BattleNight.playersInLounge))
                    && (plugin.teamReady(BattleNight.getBattle().usersTeam.get(player
                            .getName())))) {
                final Team team = BattleNight.getBattle().usersTeam.get(name);

                if (team.equals(Team.RED)) {
                    plugin.redTeamIronClicked = true;
                    BattleNight.tellEveryone(ChatColor.RED + "Red " + ChatColor.WHITE + "team is ready!");

                    if ((plugin.teamReady(Team.BLUE)) && (plugin.blueTeamIronClicked)) {
                        BattleNight.playersInLounge = false;
                        BattleNight.getBattle().start();
                    }
                } else if (team.equals(Team.BLUE)) {
                    plugin.blueTeamIronClicked = true;
                    BattleNight.tellEveryone(ChatColor.BLUE + "Blue " + ChatColor.WHITE + "team is ready!");

                    if ((plugin.teamReady(Team.RED)) && (plugin.redTeamIronClicked)) {
                        BattleNight.playersInLounge = false;
                        BattleNight.getBattle().start();
                    }
                }
            } else if ((block.getTypeId() == Configuration.config.getInt("ReadyBlock", 42)) && (BattleNight.getBattle().usersTeam.containsKey(name) && (BattleNight.playersInLounge))) {
                player.sendMessage(ChatColor.GRAY + "[BattleNight] " + ChatColor.WHITE + "Your team have not all picked a class!");
            }
        }
    }
}