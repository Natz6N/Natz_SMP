package com.natzdev.natzsmp.impl.npc;

import com.natzdev.natzsmp.NatzSMP;
import com.natzdev.natzsmp.api.npc.Npc;
import com.natzdev.natzsmp.api.npc.NpcService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NpcServiceImpl implements NpcService {
    private final NatzSMP plugin;
    private final Map<UUID, Npc> npcs = new ConcurrentHashMap<>();

    public NpcServiceImpl(NatzSMP plugin) {
        this.plugin = plugin;
        loadAll();
    }

    private void loadAll() {
        FileConfiguration cfg = plugin.configs().get("npcs.yml");
        if (!cfg.isConfigurationSection("npcs")) return;
        for (String idStr : cfg.getConfigurationSection("npcs").getKeys(false)) {
            UUID id = UUID.fromString(idStr);
            String path = "npcs." + idStr + ".";
            String name = cfg.getString(path + "name", "NPC");
            String world = cfg.getString(path + "world", "world");
            double x = cfg.getDouble(path + "x");
            double y = cfg.getDouble(path + "y");
            double z = cfg.getDouble(path + "z");
            float yaw = (float) cfg.getDouble(path + "yaw");
            float pitch = (float) cfg.getDouble(path + "pitch");
            Location loc = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
            npcs.put(id, new SimpleNpc(id, name, loc));
        }
    }

    private void save(Npc npc) {
        FileConfiguration cfg = plugin.configs().get("npcs.yml");
        String path = "npcs." + npc.getId() + ".";
        cfg.set(path + "name", npc.getName());
        cfg.set(path + "world", npc.getLocation().getWorld().getName());
        cfg.set(path + "x", npc.getLocation().getX());
        cfg.set(path + "y", npc.getLocation().getY());
        cfg.set(path + "z", npc.getLocation().getZ());
        cfg.set(path + "yaw", npc.getLocation().getYaw());
        cfg.set(path + "pitch", npc.getLocation().getPitch());
        plugin.configs().save("npcs.yml");
    }

    @Override
    public Npc create(String name, Location location) {
        SimpleNpc npc = new SimpleNpc(UUID.randomUUID(), name, location.clone());
        npcs.put(npc.getId(), npc);
        save(npc);
        return npc;
    }

    @Override
    public Optional<Npc> get(UUID id) {
        return Optional.ofNullable(npcs.get(id));
    }

    @Override
    public void remove(UUID id) {
        npcs.remove(id);
        FileConfiguration cfg = plugin.configs().get("npcs.yml");
        cfg.set("npcs." + id, null);
        plugin.configs().save("npcs.yml");
    }

    @Override
    public void spawnFor(Player player) {
        // No-op skeleton: Packet spawn would be here
    }

    @Override
    public void despawnFor(Player player) {
        // No-op skeleton
    }

    @Override
    public Collection<Npc> all() {
        return Collections.unmodifiableCollection(npcs.values());
    }

    private static class SimpleNpc implements Npc {
        private final UUID id;
        private final String name;
        private final Location location;

        SimpleNpc(UUID id, String name, Location location) {
            this.id = id;
            this.name = name;
            this.location = location;
        }

        @Override public UUID getId() { return id; }
        @Override public String getName() { return name; }
        @Override public Location getLocation() { return location.clone(); }
        @Override public void show(Player player) { }
        @Override public void hide(Player player) { }
    }
}
