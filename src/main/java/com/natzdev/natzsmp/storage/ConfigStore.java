package com.natzdev.natzsmp.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigStore {
    private final JavaPlugin plugin;
    private final Map<String, FileConfiguration> cache = new ConcurrentHashMap<>();

    public ConfigStore(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration get(String name) {
        return cache.computeIfAbsent(name, n -> {
            File file = new File(plugin.getDataFolder(), n);
            if (!file.exists()) {
                plugin.saveResource(n, false);
            }
            return YamlConfiguration.loadConfiguration(file);
        });
    }

    public void save(String name) {
        FileConfiguration cfg = cache.get(name);
        if (cfg == null) return;
        try {
            cfg.save(new File(plugin.getDataFolder(), name));
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save " + name + ": " + e.getMessage());
        }
    }
}
