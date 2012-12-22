package me.limebyte.battlenight.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.limebyte.battlenight.core.util.SafeTeleporter;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.kitteh.tag.TagAPI;

public class PlayerData {
    private static Map<String, PlayerData> storage = new HashMap<String, PlayerData>();

    private Set<String> vanishedTo = new HashSet<String>();
    private Collection<PotionEffect> potionEffects;
    private boolean allowFlight;
    private Location bedSpawnLocation;
    private Location compassTarget;
    private String displayName;
    private ItemStack[] enderItems;
    private float exaustion;
    private float exp;
    private float fallDistance;
    private int fireTicks;
    private float flySpeed;
    private int foodLevel;
    private int gameMode;
    private int health;
    private ItemStack[] invItems;
    private ItemStack[] invArmour;
    private int level;
    private Location location;
    private String playerListName;
    private long playerTimeOffset;
    private int remainingAir;
    private float saturation;
    private int ticksLived;
    private Vector velocity;
    private float walkSpeed;
    private boolean flying;
    private boolean playerTimeRelative;
    private boolean sleepingIgnored;
    private boolean sneaking;
    private boolean sprinting;

    public static void store(Player player) {
        PlayerData data = new PlayerData();

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (!player.canSee(p)) {
                data.vanishedTo.add(p.getName());
            }
        }

        data.potionEffects = player.getActivePotionEffects();
        data.allowFlight = player.getAllowFlight();
        data.bedSpawnLocation = player.getBedSpawnLocation();
        data.compassTarget = player.getCompassTarget();
        data.displayName = player.getDisplayName();
        data.enderItems = player.getEnderChest().getContents();
        data.exaustion = player.getExhaustion();
        data.exp = player.getExp();
        data.fallDistance = player.getFallDistance();
        data.fireTicks = player.getFireTicks();
        data.flySpeed = player.getFlySpeed();
        data.foodLevel = player.getFoodLevel();
        data.gameMode = player.getGameMode().getValue();
        data.health = player.getHealth();
        data.invItems = player.getInventory().getContents();
        data.invArmour = player.getInventory().getArmorContents();
        data.level = player.getLevel();
        data.location = player.getLocation();
        data.playerListName = player.getPlayerListName();
        data.playerTimeOffset = player.getPlayerTimeOffset();
        data.remainingAir = player.getRemainingAir();
        data.saturation = player.getSaturation();
        data.ticksLived = player.getTicksLived();
        data.velocity = player.getVelocity();
        data.walkSpeed = player.getWalkSpeed();
        data.flying = player.isFlying();
        data.playerTimeRelative = player.isPlayerTimeRelative();
        data.sleepingIgnored = player.isSleepingIgnored();
        data.sneaking = player.isSneaking();
        data.sprinting = player.isSprinting();

        storage.put(player.getName(), data);
    }

    public static boolean restore(Player player, boolean keepInMemory) {
        String name = player.getName();
        if (!storage.containsKey(name)) return false;

        PlayerData data = storage.get(name);

        SafeTeleporter.tp(player, data.location);

        for (String n : data.vanishedTo) {
            Player p = Bukkit.getPlayerExact(n);
            if (p != null) {
                player.hidePlayer(p);
            }
        }

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.addPotionEffect(new PotionEffect(effect.getType(), 0, 0), true);
        }

        player.addPotionEffects(data.potionEffects);
        player.setAllowFlight(data.allowFlight);

        if (data.bedSpawnLocation != null) {
            player.setBedSpawnLocation(data.bedSpawnLocation);
        }

        player.setCompassTarget(data.compassTarget);
        player.setDisplayName(data.displayName);
        player.getEnderChest().setContents(data.enderItems);
        player.setExhaustion(data.exaustion);
        player.setExp(data.exp);
        player.setFallDistance(data.fallDistance);
        player.setFireTicks(data.fireTicks);
        player.setFlySpeed(data.flySpeed);
        player.setFoodLevel(data.foodLevel);
        player.setGameMode(GameMode.getByValue(data.gameMode));
        player.setHealth(data.health);
        player.getInventory().setContents(data.invItems);
        player.getInventory().setArmorContents(data.invArmour);
        player.setLevel(data.level);
        player.setPlayerListName(data.playerListName);

        if (data.playerTimeRelative) {
            player.setPlayerTime(data.location.getWorld().getTime() + data.playerTimeOffset, true);
        }

        player.setRemainingAir(data.remainingAir);
        player.setSaturation(data.saturation);
        player.setTicksLived(data.ticksLived);
        player.setVelocity(data.velocity);
        player.setWalkSpeed(data.walkSpeed);
        player.setFlying(data.flying);
        player.setSleepingIgnored(data.sleepingIgnored);
        player.setSneaking(data.sneaking);
        player.setSprinting(data.sprinting);

        TagAPI.refreshPlayer(player);

        if (!keepInMemory) storage.remove(name);
        return true;
    }
}
