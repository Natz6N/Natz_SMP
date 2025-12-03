package com.natzdev.natzsmp.api.npc;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface NpcService {
    Npc create(String name, Location location);
    Optional<Npc> get(UUID id);
    void remove(UUID id);

    void spawnFor(Player player);
    void despawnFor(Player player);

    Collection<Npc> all();
}
