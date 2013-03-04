package me.limebyte.battlenight.core.listeners;

import me.limebyte.battlenight.api.BattleNightAPI;
import me.limebyte.battlenight.api.battle.Battle;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener extends APIRelatedListener {

    public BlockListener(BattleNightAPI api) {
        super(api);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!shouldPrevent(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        if (!shouldPrevent(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        Player player = event.getPlayer();
        if (!shouldPrevent(player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!shouldPrevent(event.getPlayer())) return;
        event.setCancelled(true);
    }

    private boolean shouldPrevent(Player player) {
        if (player == null) return false;
        Battle battle = getAPI().getBattle();
        if (battle.containsPlayer(player) && !battle.isInProgress()) return true;
        if (getAPI().getSpectatorManager().getSpectators().contains(player.getName())) return true;
        return false;
    }

}
