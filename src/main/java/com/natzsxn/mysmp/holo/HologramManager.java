package com.natzsxn.mysmp.holo;

import com.natzsxn.mysmp.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class HologramManager {
    private final JavaPlugin plugin;
    private final ConfigManager config;
    private final NamespacedKey KEY_TYPE;

    public HologramManager(JavaPlugin plugin, ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
        this.KEY_TYPE = new NamespacedKey(plugin, "mysmp_holo_type");
    }

    public void spawnAll() {
        cleanupTagged();
        spawnAnnouncement();
    }

    public void reloadAll() {
        cleanupTagged();
        spawnAll();
    }

    private World resolveWorld() {
        String lobby = config.getLobbyWorldName();
        World w = Bukkit.getWorld(lobby);
        if (w == null) {
            Bukkit.getLogger().warning("MySMP: Lobby world '" + lobby + "' not found; Holograms not spawned.");
        }
        return w;
    }

    private void spawnAnnouncement() {
        World w = resolveWorld(); if (w == null) return;
        Location base = new Location(w, -26, 86, -37);
        spawnLine(base.clone().add(0, 0.0, 0), "§e§lPengumuman", "announce_l1");
        spawnLine(base.clone().add(0, -0.35, 0), "§7Ini adalah pengumuman untuk rule survival", "announce_l2");
        spawnLine(base.clone().add(0, -0.70, 0), "§f(diisi rule yang harus ditaati)", "announce_l3");
    }

    private void spawnLine(Location loc, String text, String type) {
        ArmorStand a = loc.getWorld().spawn(loc, ArmorStand.class, s -> {
            s.setInvisible(true);
            s.setMarker(true);
            s.setGravity(false);
            s.setInvulnerable(true);
            s.setCustomNameVisible(true);
            s.setCustomName(text);
            s.getPersistentDataContainer().set(KEY_TYPE, PersistentDataType.STRING, type);
        });
    }

    private void cleanupTagged() {
        for (World w : Bukkit.getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (e.getPersistentDataContainer().has(KEY_TYPE, PersistentDataType.STRING)) {
                    e.remove();
                }
            }
        }
    }
}
