package me.limebyte.battlenight.api.battle;

import org.bukkit.ChatColor;

public class Team {

    private String name;
    private String displayName;
    private ChatColor colour;
    private boolean ready = false;
    private int lives = Battle.INFINITE_LIVES;
    private int kills = 0;
    private int size = 0;

    public Team(String name) {
        this(name, ChatColor.WHITE);
    }

    public Team(String name, ChatColor colour) {
        this.name = name.toLowerCase();
        this.displayName = name;
        this.colour = colour;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ChatColor getColour() {
        return colour;
    }

    public void setColour(ChatColor colour) {
        this.colour = colour;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        if (lives < 0) return;
        if (lives > Integer.MAX_VALUE) return;
        if (lives == Battle.INFINITE_LIVES) return;

        this.lives = lives;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void addKill() {
        kills++;
    }

    public int getSize() {
        return size;
    }

    protected void incrementSize() {
        size++;
    }

    protected void decrementSize() {
        size--;
    }

    @Override
    public String toString() {
        return colour + name;
    }

    protected void reset(Battle battle) {
        ready = false;
        lives = battle.getBattleLives();
        kills = 0;
        size = 0;
    }

}
