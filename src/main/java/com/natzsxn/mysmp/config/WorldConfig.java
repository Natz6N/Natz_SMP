// FILE: src/main/java/com/natzsxn/mysmp/config/WorldConfig.java
package com.natzsxn.mysmp.config;

import com.natzsxn.mysmp.world.SpawnLocation;
import com.natzsxn.mysmp.world.WorldType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class WorldConfig {
    private final JavaPlugin plugin;
    private FileConfiguration cfg;

    public WorldConfig(JavaPlugin plugin) { this.plugin = plugin; }

    public void load() {
        File file = new File(plugin.getDataFolder(), "worlds.yml");
        if (!file.exists()) plugin.saveResource("worlds.yml", false);
        cfg = YamlConfiguration.loadConfiguration(file);
    }

    public CompletableFuture<SpawnLocation> getSpawn(WorldType type) {
        return CompletableFuture.supplyAsync(() -> {
            String base = type == WorldType.LOBBY ? "lobby.spawn" : "survival.spawn";
            String world = cfg.getString(base + ".world");
            double x = cfg.getDouble(base + ".x");
            double y = cfg.getDouble(base + ".y");
            double z = cfg.getDouble(base + ".z");
            return new SpawnLocation(world, x, y, z, 0f, 0f);
        });
    }
}
