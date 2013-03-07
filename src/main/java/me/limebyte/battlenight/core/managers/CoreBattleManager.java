package me.limebyte.battlenight.core.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import me.limebyte.battlenight.api.BattleNightAPI;
import me.limebyte.battlenight.api.battle.Battle;
import me.limebyte.battlenight.api.managers.BattleManager;
import me.limebyte.battlenight.core.FFABattle;
import me.limebyte.battlenight.core.TDMBattle;
import me.limebyte.battlenight.core.util.Messenger;
import me.limebyte.battlenight.core.util.config.ConfigManager;
import me.limebyte.battlenight.core.util.config.ConfigManager.Config;

public class CoreBattleManager implements BattleManager {

    private BattleNightAPI api;
    private String activeBattle;
    private Map<String, Battle> battles = new HashMap<String, Battle>();

    public CoreBattleManager(BattleNightAPI api) {
        this.api = api;

        // Defaults
        int time = getDuration();
        registerBattle(new TDMBattle(time), "TDM");
        registerBattle(new FFABattle(time), "FFA");

        reload();
    }

    @Override
    public void registerBattle(Battle battle, String id) {
        if (battle == null || battles.containsKey(id)) throw new IllegalArgumentException();
        battle.api = api;
        battles.put(id, battle);
    }

    @Override
    public boolean deregisterBattle(String id) {
        if (activeBattle.equals(id)) throw new IllegalStateException();
        if (!battles.containsKey(id)) return false;
        battles.remove(id);
        return true;
    }

    @Override
    public Battle getBattle(String id) {
        return battles.get(id);
    }

    @Override
    public List<Battle> getBattles() {
        return new ArrayList<Battle>(battles.values());
    }

    @Override
    public Battle getActiveBattle() {
        return getBattle(activeBattle);
    }

    @Override
    public boolean setActiveBattle(String id) {
        if (!battles.containsKey(id)) return false;

        Battle active = getActiveBattle();
        if (active != null && active.isInProgress()) throw new IllegalStateException();

        activeBattle = id;
        return true;
    }

    public void reload() {
        String battle = ConfigManager.get(Config.MAIN).getString("Battle.Type", "TDM");
        if (getBattle(battle) == null) battle = "TDM";
        setActiveBattle(battle);

        for (Battle b : battles.values()) {
            b.getTimer().setTime(getDuration());
        }
    }

    public int getDuration() {
        String value = ConfigManager.get(Config.MAIN).getString("Battle.Duration", "5:00");
        String[] time = value.split(":");

        if (time.length < 2) {
            Messenger.log(Level.WARNING, "Invalid battle duration: " + value);
            return 300;
        }

        int mins = Integer.parseInt(time[0]);
        int secs = Integer.parseInt(time[1]);

        return mins * 60 + secs;
    }
}
