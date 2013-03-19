package me.limebyte.battlenight.core.tosort;

import java.util.List;

import me.limebyte.battlenight.api.managers.ClassManager;
import me.limebyte.battlenight.api.util.PlayerClass;
import me.limebyte.battlenight.core.BattleNight;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class Metadata {

    public static boolean getBoolean(Player player, String key) {
        MetadataValue value = getValue(player, key);
        if (value != null) return value.asBoolean();
        return false;
    }

    public static int getInt(Player player, String key) {
        MetadataValue value = getValue(player, key);
        if (value != null) return value.asInt();
        return 0;
    }

    public static PlayerClass getPlayerClass(Player player) {
        String result = getString(player, "class");
        if (result == null) return null;

        ClassManager manager = BattleNight.instance.getAPI().getClassManager();
        for (PlayerClass clazz : manager.getClasses()) {
            if (result.equals(clazz.getName())) return clazz;
        }
        return null;
    }

    public static String getString(Player player, String key) {
        MetadataValue value = getValue(player, key);
        if (value != null) return value.asString();
        return null;
    }

    private static MetadataValue getValue(Player player, String key) {
        List<MetadataValue> values = player.getMetadata(key);
        String bnName = BattleNight.instance.getDescription().getName();

        for (MetadataValue value : values) {
            String owner = value.getOwningPlugin().getDescription().getName();
            if (owner == bnName) return value;
        }

        return null;
    }

    public static void remove(Player player, String key) {
        player.removeMetadata(key, BattleNight.instance);
    }

    public static void set(Player player, String key, Object value) {
        player.setMetadata(key, new FixedMetadataValue(BattleNight.instance, value));
    }

}
