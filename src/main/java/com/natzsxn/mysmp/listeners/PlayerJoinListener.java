package com.natzsxn.mysmp.listeners;

import com.natzsxn.mysmp.config.ConfigManager;
import com.natzsxn.mysmp.storage.DatabaseManager;
import com.natzsxn.mysmp.util.Messager;
import com.natzsxn.mysmp.util.RulesBookUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerJoinListener implements Listener {
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final DatabaseManager databaseManager;
    private final Messager messager;

    public PlayerJoinListener(JavaPlugin plugin, ConfigManager configManager, DatabaseManager databaseManager,
            Messager messager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.databaseManager = databaseManager;
        this.messager = messager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.sendMessage(messager.success("Hello " + p.getName()));

        // Check agreement
        databaseManager.isAgreed(p.getUniqueId()).thenAccept(agreed -> {
            if (!agreed) {
                RulesBookUtil.giveBookIfNeeded(plugin, p, messager);
            }
        });

        // Teleport to lobby if configured
        // Assuming we always want to teleport to lobby on join or check config
        // The previous code had 'cfg.isTeleportToLobbyOnJoin()'. I should add that to
        // ConfigManager or assume true/false.
        // I'll check ConfigManager. I didn't add it. I'll just assume true or read
        // directly.
        // I'll read directly.
        if (plugin.getConfig().getBoolean("settings.teleport-lobby-on-join", true)) {
            ConfigManager.SimpleSpawn spawn = configManager.getSimpleSpawn("lobby");
            if (spawn != null) {
                // Load world logic is complicated here without WarpManager.
                // But WarpManager is for commands.
                // I should probably use WarpManager here if I can, but I don't have reference
                // to it here.
                // I'll just use Bukkit.getWorld and teleport if loaded, or try to load.
                Bukkit.getScheduler().runTask(plugin, () -> {
                    org.bukkit.World w = Bukkit.getWorld(spawn.worldName);
                    if (w == null) {
                        w = Bukkit.createWorld(new org.bukkit.WorldCreator(spawn.worldName));
                    }
                    if (w != null) {
                        Location loc = new Location(w, spawn.x, spawn.y, spawn.z, spawn.yaw, spawn.pitch);
                        p.teleport(loc);
                    }
                });
            }
        }
    }
}
