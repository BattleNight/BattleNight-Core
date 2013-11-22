package me.limebyte.battlenight.core.tosort;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    public static final String DATA_DIRECTORY = ".PluginData";

    private static Configuration mainConfig = new Configuration("Config.yml");
    private static Configuration classesConfig = new Configuration("Classes.yml", false);
    private static Configuration metricsConfig = new Configuration("Metrics.yml");
    private static Configuration arenasConfig = new Configuration("Arenas.dat", DATA_DIRECTORY);
    private static Configuration messagesConfig = new Configuration("messages.yml");

    public static FileConfiguration get(Config config) {
        return config.getConfiguration().get();
    }

    public static void initConfigurations() {
        reloadAll();
        saveAll();
    }

    public static void reload(Config config) {
        config.getConfiguration().reload();
    }

    public static void reloadAll() {
        for (Config configFile : Config.values()) {
            configFile.getConfiguration().reload();
        }
    }

    public static void save(Config config) {
        config.getConfiguration().save();
    }

    public static void saveAll() {
        for (Config configFile : Config.values()) {
            configFile.getConfiguration().save();
        }
    }

    public enum Config {
        MAIN(mainConfig),
        CLASSES(classesConfig),
        METRICS(metricsConfig),
        ARENAS(arenasConfig),
        MESSAGES(messagesConfig);

        private Configuration config;

        Config(Configuration config) {
            this.config = config;
        }

        public Configuration getConfiguration() {
            return config;
        }
    }

}
