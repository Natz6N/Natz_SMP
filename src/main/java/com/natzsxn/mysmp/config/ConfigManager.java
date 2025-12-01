package com.natzsxn.mysmp.config;

import com.natzsxn.mysmp.util.Messager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * ConfigManager - Manages plugin configuration.
 * Handles loading, saving, and accessing settings.
 * Stores spawn locations in config.yml.
 */
public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Load all configurations
     */
    public void loadAll() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    public String getPrefix() {
        return config.getString("prefix", "&a[MySMP] ");
    }

    /**
     * Get the configured lobby world name
     * @return Lobby world name
     */
    public String getLobbyWorldName() {
        return config.getString("settings.lobby-world", "lobby");
    }

    /**
     * Get the configured survival world name
     * @return Survival world name
     */
    public String getSurvivalWorldName() {
        return config.getString("settings.survival-world", "survival");
    }

    /**
     * Get the owner UUID from configuration
     * @return Owner UUID as string
     */
    public String getOwnerUUID() {
        return config.getString("settings.owner-uuid", "");
    }
    public String getOwnerUsername() {
        return config.getString("settings.owner-username", "");
    }

    /**
     * Set owner UUID di konfigurasi dan simpan file
     */
    public void setOwnerUUID(String uuid) {
        config.set("settings.owner-uuid", uuid);
        plugin.saveConfig();
    }

    /**
     * Set the spawn location for a world type
     * @param type "lobby" or "survival"
     * @param loc Location to set
     */
    public void setSpawn(String type, Location loc) {
        String path = "locations." + type.toLowerCase();
        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());
        plugin.saveConfig();
    }

    /**
     * Get the spawn location for a world type
     * @param type "lobby" or "survival"
     * @return Location or null if not set
     */
    public Location getSpawn(String type) {
        String path = "locations." + type.toLowerCase();
        if (!config.contains(path + ".world")) return null;

        String worldName = config.getString(path + ".world");
        if (worldName == null) return null;

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null; 
        }

        double x = config.getDouble(path + ".x");
        double y = config.getDouble(path + ".y");
        double z = config.getDouble(path + ".z");
        float yaw = (float) config.getDouble(path + ".yaw");
        float pitch = (float) config.getDouble(path + ".pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Get spawn info even if world is not loaded (for loading purposes)
     * @param type "lobby" or "survival"
     * @return SpawnLocation DTO
     */
    public SimpleSpawn getSimpleSpawn(String type) {
        String path = "locations." + type.toLowerCase();
        if (!config.contains(path + ".world")) return null;

        String worldName = config.getString(path + ".world");
        double x = config.getDouble(path + ".x");
        double y = config.getDouble(path + ".y");
        double z = config.getDouble(path + ".z");
        float yaw = (float) config.getDouble(path + ".yaw");
        float pitch = (float) config.getDouble(path + ".pitch");

        return new SimpleSpawn(worldName, x, y, z, yaw, pitch);
    }
    
    public void reloadAll() {
        loadAll();
    }

    public static class SimpleSpawn {
        public final String worldName;
        public final double x, y, z;
        public final float yaw, pitch;

        public SimpleSpawn(String worldName, double x, double y, double z, float yaw, float pitch) {
            this.worldName = worldName;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }
}
