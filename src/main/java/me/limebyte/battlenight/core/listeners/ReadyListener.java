package me.limebyte.battlenight.core.listeners;

import me.limebyte.battlenight.core.BattleNight;
import me.limebyte.battlenight.core.battle.Team;
import me.limebyte.battlenight.core.util.chat.Messaging;
import me.limebyte.battlenight.core.util.chat.Messaging.Message;
import me.limebyte.battlenight.core.util.config.ConfigManager;
import me.limebyte.battlenight.core.util.config.ConfigManager.Config;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ReadyListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();

            if (block.getTypeId() == ConfigManager.get(Config.MAIN).getInt("ReadyBlock", 42)) {
                if (BattleNight.getBattle().usersTeam.containsKey(player.getName()) && BattleNight.getBattle().isInLounge()) {
                    String name = player.getName();
                    Team team = BattleNight.getBattle().usersTeam.get(name);

                    if (team.isReady()) {
                        Messaging.tellEveryone(Message.TEAM_IS_READY, false, team.getColour() + team.getName());

                        if (team.equals(Team.RED)) {
                            BattleNight.redTeamIronClicked = true;

                            if (Team.BLUE.isReady() && BattleNight.blueTeamIronClicked) {
                                BattleNight.getBattle().start();
                            }
                        } else if (team.equals(Team.BLUE)) {
                            BattleNight.blueTeamIronClicked = true;

                            if (Team.RED.isReady() && BattleNight.redTeamIronClicked) {
                                BattleNight.getBattle().start();
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.GRAY + "[BattleNight] " + ChatColor.WHITE + "Your team have not all picked a class!");
                    }
                }
            }
        }
    }
}