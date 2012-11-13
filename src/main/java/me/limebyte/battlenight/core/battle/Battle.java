package me.limebyte.battlenight.core.battle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import me.limebyte.battlenight.core.BattleNight;
import me.limebyte.battlenight.core.listeners.SignListener;
import me.limebyte.battlenight.core.other.Tracks.Track;
import me.limebyte.battlenight.core.util.Metadata;
import me.limebyte.battlenight.core.util.SafeTeleporter;
import me.limebyte.battlenight.core.util.chat.Messaging;
import me.limebyte.battlenight.core.util.chat.Messaging.Message;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.kitteh.tag.TagAPI;

public class Battle {

    private BattleNight plugin;
    private int redTeam = 0;
    private int blueTeam = 0;
    private boolean inLounge = false;
    private boolean inProgress = false;
    private boolean ending = false;

    public final Map<String, Team> usersTeam = new HashMap<String, Team>();
    public final Set<String> spectators = new HashSet<String>();

    public Battle() {
        this.plugin = BattleNight.instance;
    }

    public void addPlayer(Player player) {
        if (plugin.preparePlayer(player)) {
            String name = player.getName();
            Team team;

            if (blueTeam > redTeam) {
                team = Team.RED;
                redTeam++;
                SafeTeleporter.tp(player, Waypoint.RED_LOUNGE);
            } else {
                team = Team.BLUE;
                blueTeam++;
                SafeTeleporter.tp(player, Waypoint.BLUE_LOUNGE);
            }

            usersTeam.put(name, team);
            Messaging.tell(player, "Welcome! You are on team " + team.getColour() + team.getName() + ChatColor.WHITE + ".");
            Messaging.tellEveryoneExcept(player, name + " has joined team " + team.getColour() + team.getName() + ChatColor.WHITE + ".", true);

            BattleNight.setNames(player);
            inLounge = true;
        } else {
            Messaging.tell(player, Track.MUST_HAVE_EMPTY.msg);
        }
    }

    public void removePlayer(Player player, boolean death, String msg1, String msg2) {
        String name = player.getName();

        if (usersTeam.containsKey(name)) {
            Team team = usersTeam.get(name);
            boolean sendMsg1 = msg1 != null;

            if (team.equals(Team.RED)) {
                redTeam--;
            } else if (team.equals(Team.BLUE)) {
                blueTeam--;
            }

            if (sendMsg1) Messaging.tellEveryoneExcept(player, team.getColour() + name + ChatColor.WHITE + " " + msg1, true);

            if (msg2 != null) {
                Messaging.tell(player, msg2);
            }

            // If red or blue won
            if (redTeam == 0 || blueTeam == 0) {

                ending = true;

                // If the battle started
                if (!inLounge) {
                    // If red won
                    if (redTeam > 0) {
                        Messaging.tellEveryone(Message.TEAM_WON, true, Team.RED.getColour() + Team.RED.getName());
                        // If blue won
                    } else if (blueTeam > 0) {
                        Messaging.tellEveryone(Message.TEAM_WON, true, Team.BLUE.getColour() + Team.BLUE.getName());
                        // If neither team won
                    } else {
                        Messaging.tellEveryone(Message.DRAW, true);
                    }
                }

                Iterator<String> it = usersTeam.keySet().iterator();
                while (it.hasNext()) {
                    String currentName = it.next();
                    if (Bukkit.getPlayerExact(currentName) != null) {
                        Player currentPlayer = Bukkit.getPlayerExact(currentName);
                        if (!death) {
                            resetPlayer(currentPlayer, true, it);
                        } else {
                            if (currentPlayer != player) {
                                resetPlayer(currentPlayer, true, it);
                            }
                        }
                    }
                }

                resetBattle();
            }

            if (!death) resetPlayer(player, true, null);
        } else {
            BattleNight.log.warning("Failed to remove player '" + name + "' from the Battle as they are not in it.");
        }
    }

    public void resetPlayer(Player player, boolean teleport, Iterator<String> it) {
        player.getInventory().clear();
        if (teleport) SafeTeleporter.tp(player, Waypoint.EXIT);
        plugin.restorePlayer(player);
        SignListener.cleanSigns(player);
        Metadata.remove(player, "class");

        if (it != null) {
            it.remove();
        } else {
            usersTeam.remove(player.getName());
        }

        try {
            TagAPI.refreshPlayer(player);
        } catch (Exception e) {
        }
    }

    private void resetBattle() {
        plugin.removeAllSpectators();
        SignListener.cleanSigns();
        inProgress = false;
        inLounge = false;
        ending = false;
        plugin.redTeamIronClicked = false;
        plugin.blueTeamIronClicked = false;
        usersTeam.clear();
        redTeam = 0;
        blueTeam = 0;
    }

    public void start() {
        inProgress = true;
        inLounge = false;
        Messaging.tellEveryone(Message.BATTLE_STARTED, true);
        plugin.teleportAllToSpawn();
        SignListener.cleanSigns();
    }

    public void stop() {
        if (!inLounge) {
            if (blueTeam > redTeam) {
                Messaging.tellEveryone(Message.TEAM_WON, true, Team.BLUE.getColour() + Team.BLUE.getName());
            } else if (redTeam > blueTeam) {
                Messaging.tellEveryone(Message.TEAM_WON, true, Team.RED.getColour() + Team.RED.getName());
            } else {
                Messaging.tellEveryone(Message.DRAW, true);
            }
        }

        Iterator<String> it = usersTeam.keySet().iterator();
        while (it.hasNext()) {
            String currentName = it.next();
            if (Bukkit.getPlayerExact(currentName) != null) {
                Player currentPlayer = Bukkit.getPlayerExact(currentName);
                resetPlayer(currentPlayer, true, it);
            }
        }

        resetBattle();

        plugin.removeAllSpectators();
    }

    public boolean isInLounge() {
        return inLounge;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public boolean isEnding() {
        return ending;
    }
}
