// FILE: src/main/java/com/natzsxn/mysmp/service/TeleportService.java
package com.natzsxn.mysmp.service;

import com.natzsxn.mysmp.storage.WorldDataStorage;
import com.natzsxn.mysmp.task.TeleportCooldownTask;
import com.natzsxn.mysmp.util.Messager;
import com.natzsxn.mysmp.world.WorldType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TeleportService {
    private final WorldDataStorage storage;
    private final TeleportCooldownTask cooldowns;
    private final Messager messager;

    public TeleportService(WorldDataStorage storage, TeleportCooldownTask cooldowns, Messager messager) {
        this.storage = storage;
        this.cooldowns = cooldowns;
        this.messager = messager;
    }

    public CompletableFuture<Boolean> teleportTo(Player p, WorldType type) {
        UUID id = p.getUniqueId();
        if (!cooldowns.tryAcquire(id, 5)) {
            p.sendMessage(messager.warn("Please wait before teleporting again"));
            return CompletableFuture.completedFuture(false);
        }
        return storage.getSpawn(type).thenCompose(spawn -> {
            if (spawn == null) {
                p.sendMessage(messager.error(type == WorldType.LOBBY ? "Lobby spawn not set" : "Survival spawn not set"));
                return CompletableFuture.completedFuture(false);
            }
            Location loc = spawn.toLocation();
            return p.teleportAsync(loc).thenApply(success -> {
                if (success) {
                    p.sendMessage(messager.success(type == WorldType.LOBBY ? "Teleported to lobby" : "Teleported to survival"));
                } else {
                    p.sendMessage(messager.error("Teleport failed"));
                }
                return success;
            });
        });
    }
}
