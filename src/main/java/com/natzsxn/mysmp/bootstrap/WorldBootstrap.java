// FILE: src/main/java/com/natzsxn/mysmp/bootstrap/WorldBootstrap.java
package com.natzsxn.mysmp.bootstrap;

import com.natzsxn.mysmp.Main;
import com.natzsxn.mysmp.config.ConfigManager;
import com.natzsxn.mysmp.storage.WorldDataStorage;
import com.natzsxn.mysmp.util.Messager;
import com.natzsxn.mysmp.world.SpawnLocation;
import com.natzsxn.mysmp.world.WorldManager;
import com.natzsxn.mysmp.world.WorldType;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class WorldBootstrap {
    private final Main plugin;
    private final ServiceLocator services;

    public WorldBootstrap(Main plugin, ServiceLocator services) {
        this.plugin = plugin;
        this.services = services;
    }

    public void ensureWorlds() {
        ConfigManager cfg = services.get(ConfigManager.class);
        WorldManager wm = services.get(WorldManager.class);
        WorldDataStorage storage = services.get(WorldDataStorage.class);
        Messager msg = services.get(Messager.class);

        cfg.getWorldsConfig().getSpawn(WorldType.LOBBY).thenAccept(spawn -> {
            if (spawn != null) {
                wm.loadOrCreateWorld(spawn.getWorldName()).thenAccept(world -> {
                    storage.setSpawn(WorldType.LOBBY, spawn);
                });
            }
        });

        cfg.getWorldsConfig().getSpawn(WorldType.SURVIVAL).thenAccept(spawn -> {
            if (spawn != null) {
                wm.loadOrCreateWorld(spawn.getWorldName()).thenAccept(world -> {
                    storage.setSpawn(WorldType.SURVIVAL, spawn);
                });
            }
        });

        Bukkit.getScheduler().runTask(plugin, () -> {
            World lobby = Bukkit.getWorld(cfg.getLobbyWorldName());
            if (lobby == null) {
                wm.loadOrCreateWorld(cfg.getLobbyWorldName());
            }
            msg.info("Worlds verified");
        });
    }
}
