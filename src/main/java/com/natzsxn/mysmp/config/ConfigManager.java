// FILE: src/main/java/com/natzsxn/mysmp/config/ConfigManager.java
package com.natzsxn.mysmp.config;

import com.natzsxn.mysmp.world.WorldType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class ConfigManager {
    private final JavaPlugin plugin;
    private Messages messagesConfig;
    private WorldConfig worldsConfig;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        messagesConfig = new Messages(plugin);
        messagesConfig.load();
        worldsConfig = new WorldConfig(plugin);
        worldsConfig.load();
    }

    public void reloadAll() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        messagesConfig.load();
        worldsConfig.load();
    }

    public FileConfiguration getConfig() { return config; }
    public Messages getMessagesConfig() { return messagesConfig; }
    public WorldConfig getWorldsConfig() { return worldsConfig; }

    public String getLobbyWorldName() { return config.getString("settings.lobby-world", "lobby"); }
    public boolean isTeleportToLobbyOnJoin() { return config.getBoolean("settings.teleport-lobby-on-join", true); }
}
