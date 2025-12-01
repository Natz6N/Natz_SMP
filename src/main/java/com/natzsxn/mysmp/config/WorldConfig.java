// FILE: src/main/java/com/natzsxn/mysmp/config/WorldConfig.java
package com.natzsxn.mysmp.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * WorldConfig - Manages world-specific configuration from worlds.yml
 * This class handles loading and accessing world spawn locations
 * 
 * NOTE: This class appears to be deprecated/unused in favor of ConfigManager
 * Consider removing or refactoring to use ConfigManager consistently
 */
public class WorldConfig {
    private final JavaPlugin plugin;
    private FileConfiguration cfg;

    /**
     * Constructor - Initializes WorldConfig with plugin instance
     * @param plugin JavaPlugin instance for accessing data folder and resources
     */
    public WorldConfig(JavaPlugin plugin) { 
        this.plugin = plugin; 
    }

    /**
     * Load worlds.yml configuration file
     * Creates default file if it doesn't exist
     */
    public void load() {
        File file = new File(plugin.getDataFolder(), "worlds.yml");
        if (!file.exists()) plugin.saveResource("worlds.yml", false);
        cfg = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Get spawn location for a specific world type (lobby/survival)
     * This method is currently unused and may need refactoring
     * 
     * @param type String representing world type ("lobby" or "survival")
     * @return CompletableFuture with SimpleSpawn containing spawn coordinates
     */
    public CompletableFuture<ConfigManager.SimpleSpawn> getSpawn(String type) {
        return CompletableFuture.supplyAsync(() -> {
            String base = type.toLowerCase() + ".spawn";
            String world = cfg.getString(base + ".world");
            double x = cfg.getDouble(base + ".x");
            double y = cfg.getDouble(base + ".y");
            double z = cfg.getDouble(base + ".z");
            float yaw = (float) cfg.getDouble(base + ".yaw", 0f);
            float pitch = (float) cfg.getDouble(base + ".pitch", 0f);
            
            return new ConfigManager.SimpleSpawn(world, x, y, z, yaw, pitch);
        });
    }
}
