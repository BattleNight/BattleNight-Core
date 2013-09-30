package me.limebyte.battlenight.core.commands;

import java.util.Arrays;

import me.limebyte.battlenight.api.battle.Lobby;
import me.limebyte.battlenight.api.util.Message;
import me.limebyte.battlenight.api.util.Messenger;
import me.limebyte.battlenight.api.util.PlayerClass;
import me.limebyte.battlenight.core.tosort.ConfigManager;
import me.limebyte.battlenight.core.tosort.ConfigManager.Config;
import me.limebyte.battlenight.core.util.ParticleEffect;
import me.limebyte.battlenight.core.util.player.Metadata;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClassCommand extends BattleNightCommand {

    protected ClassCommand() {
        super("Class");

        setLabel("class");
        setDescription("Selects a class.");
        setUsage("/bn class <class>");
        setPermission(CommandPermission.USER);
        setAliases(Arrays.asList("equip"));
    }

    @Override
    protected boolean onPerformed(CommandSender sender, String[] args) {
        Messenger messenger = api.getMessenger();
        Lobby lobby = api.getLobby();

        if (!(sender instanceof Player)) {
            api.getMessenger().tell(sender, Message.PLAYER_ONLY);
            return false;
        }

        Player player = (Player) sender;

        if (!lobby.contains(player)) {
            messenger.tell(sender, Message.NOT_IN_LOBBY);
            return false;
        }

        if (args.length < 1) {
            messenger.tell(sender, Message.SPECIFY_CLASS);
            messenger.tell(sender, Message.USAGE, getUsage());
            return false;
        }

        PlayerClass playerClass = api.getClassManager().getPlayerClass(args[0]);

        if (playerClass == null) {
            messenger.tell(sender, Message.INVALID_CLASS);
            return false;
        }

        if (player.hasPermission(playerClass.getPermission())) {
            if (Metadata.getPlayerClass(player) != playerClass) {
                ParticleEffect.classSelect(player, ConfigManager.get(Config.MAIN).getString("Particles.ClassSelection", "smoke"));
            }

            api.setPlayerClass(player, playerClass);
            return true;
        } else {
            messenger.tell(player, Message.NO_PERMISSION_CLASS);
            return false;
        }

    }

}
