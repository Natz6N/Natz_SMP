package com.natzsxn.mysmp.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Util lokasi untuk serialisasi/deserialisasi lengkap ke/dari config.
 * Menyimpan world, x, y, z, yaw, pitch pada path yang diberikan.
 */
public class LocationUtil {
    public static void saveLocationToConfig(FileConfiguration cfg, String path, Location loc) {
        if (loc == null || loc.getWorld() == null) return;
        cfg.set(path + ".world", loc.getWorld().getName());
        cfg.set(path + ".x", loc.getX());
        cfg.set(path + ".y", loc.getY());
        cfg.set(path + ".z", loc.getZ());
        cfg.set(path + ".yaw", loc.getYaw());
        cfg.set(path + ".pitch", loc.getPitch());
    }

    public static Location loadLocationFromConfig(FileConfiguration cfg, String path) {
        String worldName = cfg.getString(path + ".world");
        if (worldName == null || worldName.isEmpty()) return null;

        double x = cfg.getDouble(path + ".x");
        double y = cfg.getDouble(path + ".y");
        double z = cfg.getDouble(path + ".z");
        float yaw = (float) cfg.getDouble(path + ".yaw", 0.0);
        float pitch = (float) cfg.getDouble(path + ".pitch", 0.0);

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            world = Bukkit.createWorld(new WorldCreator(worldName));
        }
        if (world == null) return null;

        return new Location(world, x, y, z, yaw, pitch);
    }
}
