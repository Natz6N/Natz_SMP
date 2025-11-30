// FILE: src/main/java/com/natzsxn/mysmp/world/WorldManager.java
package com.natzsxn.mysmp.world;

import com.natzsxn.mysmp.storage.WorldDataStorage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;

public class WorldManager {
    private final JavaPlugin plugin;
    private final WorldDataStorage storage;

    public WorldManager(JavaPlugin plugin, WorldDataStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    public CompletableFuture<World> loadOrCreateWorld(String name) {
        World existing = Bukkit.getWorld(name);
        if (existing != null) return CompletableFuture.completedFuture(existing);
        CompletableFuture<World> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTask(plugin, () -> {
            World w = Bukkit.getWorld(name);
            if (w == null) w = new WorldCreator(name).createWorld();
            future.complete(w);
        });
        return future;
    }
}
