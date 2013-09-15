package me.limebyte.battlenight.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import me.limebyte.battlenight.api.BattleNightAPI;
import me.limebyte.battlenight.api.BattleNightPlugin;
import me.limebyte.battlenight.api.battle.Battle;
import me.limebyte.battlenight.api.battle.Lobby;
import me.limebyte.battlenight.api.util.Messenger;
import me.limebyte.battlenight.core.battle.SimpleArena;
import me.limebyte.battlenight.core.commands.BattleNightTabCompleter;
import me.limebyte.battlenight.core.commands.CommandManager;
import me.limebyte.battlenight.core.hooks.Metrics;
import me.limebyte.battlenight.core.listeners.BlockListener;
import me.limebyte.battlenight.core.listeners.CheatListener;
import me.limebyte.battlenight.core.listeners.DeathListener;
import me.limebyte.battlenight.core.listeners.ConnectionListener;
import me.limebyte.battlenight.core.listeners.HealthListener;
import me.limebyte.battlenight.core.listeners.InteractListener;
import me.limebyte.battlenight.core.listeners.SignListener;
import me.limebyte.battlenight.core.tosort.ConfigManager;
import me.limebyte.battlenight.core.tosort.ConfigManager.Config;
import me.limebyte.battlenight.core.util.SimpleWaypoint;
import me.limebyte.battlenight.core.util.Teleporter;
import me.limebyte.battlenight.core.util.UpdateChecker;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BattleNight extends JavaPlugin implements BattleNightPlugin {

    public static BattleNight instance;
    private BattleNightAPI api;

    @Override
    public BattleNightAPI getAPI() {
        return api;
    }

    @Override
    public void onDisable() {
        Battle battle = getAPI().getBattle();
        Lobby lobby = getAPI().getLobby();

        api.getArenaManager().saveArenas();

        if (battle != null) {
            battle.stop();
        }
        for (String name : new ArrayList<String>(lobby.getPlayers())) {
            Player player = Bukkit.getPlayerExact(name);
            if (player != null) {
                lobby.removePlayer(player);
            }
        }

        getServer().getScheduler().cancelTasks(this);

        PluginDescriptionFile pdfFile = getDescription();
        api.getMessenger().log(Level.INFO, "Version " + pdfFile.getVersion() + " has been disabled.");
    }

    @Override
    public void onEnable() {
        instance = this;

        ConfigurationSerialization.registerClass(SimpleArena.class);
        ConfigurationSerialization.registerClass(SimpleWaypoint.class);
        ConfigManager.initConfigurations();

        api = new API(this);

        Messenger messenger = api.getMessenger();
        PluginManager pm = getServer().getPluginManager();
        PluginDescriptionFile pdf = getDescription();

        // Debugging
        if (ConfigManager.get(Config.MAIN).getBoolean("UsePermissions", false)) {
            messenger.debug(Level.INFO, "Permissions Enabled.");
        } else {
            messenger.debug(Level.INFO, "Permissions Disabled, using Op.");
        }

        // Commands
        PluginCommand command = getCommand("battlenight");
        CommandManager manager = new CommandManager(api);
        command.setExecutor(manager);
        command.setTabCompleter(new BattleNightTabCompleter());

        // Hooks
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
        }

        // Event Registration
        pm.registerEvents(new BlockListener(api), this);
        pm.registerEvents(new CheatListener(api), this);
        pm.registerEvents(new HealthListener(api), this);
        pm.registerEvents(new DeathListener(api), this);
        pm.registerEvents(new ConnectionListener(api), this);
        pm.registerEvents(new InteractListener(api), this);
        pm.registerEvents(new Teleporter(), this);
        pm.registerEvents(new SignListener(api), this);

        if (ConfigManager.get(Config.MAIN).getBoolean("UpdateCheck", true)) {
            UpdateChecker.check(api, pdf);
        }

        // Enable Message
        messenger.log(Level.INFO, "Version " + pdf.getVersion() + " enabled successfully.");
        messenger.log(Level.INFO, "Made by LimeByte.");
    }
}
