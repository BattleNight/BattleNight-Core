package me.limebyte.battlenight.api.managers;

import java.util.List;

import me.limebyte.battlenight.api.battle.Arena;
import me.limebyte.battlenight.api.battle.Waypoint;

public interface ArenaManager {

    public void loadArenas();

    public void saveArenas();

    public void register(Arena arena);

    public void deregister(Arena arena);

    public List<Arena> getArenas();

    public List<Arena> getEnabledArenas();

    public Arena getRandomArena();

    public Waypoint getLounge();

    public Waypoint getExit();

}
