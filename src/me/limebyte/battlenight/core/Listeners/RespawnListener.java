package me.limebyte.battlenight.core.Listeners;

import me.limebyte.battlenight.core.BattleNight;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener implements Listener {

	// Get Main Class
	public static BattleNight plugin;

	public RespawnListener(BattleNight instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		String name = player.getName();
		if (plugin.BattleUsersRespawn.containsKey(name)) {

			plugin.removePlayer(player, name, null, "You have been removed from the Battle because you were killed.",	false);

			// If the Battle is still going on, take them to the spectator area
			// to watch
			if (plugin.battleInProgress) {
				event.setRespawnLocation(plugin.getCoords("spectator"));
				plugin.addSpectator(player, "death");
				// Else, take them to the exit area
			} else {
				event.setRespawnLocation(plugin.getCoords("exit"));
			}
			plugin.BattleUsersRespawn.remove(name);
		}
	}

}