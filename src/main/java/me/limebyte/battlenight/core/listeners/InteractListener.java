package me.limebyte.battlenight.core.listeners;

import me.limebyte.battlenight.api.BattleNightAPI;
import me.limebyte.battlenight.api.battle.Battle;
import me.limebyte.battlenight.core.util.Messenger;
import me.limebyte.battlenight.core.util.Messenger.Message;
import me.limebyte.battlenight.core.util.Metadata;
import me.limebyte.battlenight.core.util.config.ConfigManager;
import me.limebyte.battlenight.core.util.config.ConfigManager.Config;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener extends APIRelatedListener {

    public InteractListener(BattleNightAPI api) {
        super(api);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        Battle battle = getAPI().getBattle();

        if (action.equals(Action.LEFT_CLICK_BLOCK)) {
            Block block = event.getClickedBlock();

            if (block.getTypeId() == ConfigManager.get(Config.MAIN).getInt("ReadyBlock", 42)) {
                if (battle.containsPlayer(player) && !battle.isInProgress()) {
                    if (getAPI().getPlayerClass(player) != null) {
                        if (!Metadata.getBoolean(player, "ready")) {
                            Metadata.set(player, "ready", true);
                            Messenger.tellEveryone(true, Message.PLAYER_IS_READY, player);
                        }

                        if (battle.getPlayers().size() < 2) return;
                        boolean allReady = true;
                        for (String n : battle.getPlayers()) {
                            Player p = Bukkit.getPlayerExact(n);
                            if (p == null) {
                                continue;
                            }
                            if (!Metadata.getBoolean(p, "ready")) {
                                allReady = false;
                                break;
                            }
                        }
                        if (allReady) {
                            battle.start();
                        }
                    } else {
                        Messenger.tell(player, Message.NO_CLASS);
                    }
                }
            }
        }
    }
}