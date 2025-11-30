// FILE: src/main/java/com/natzsxn/mysmp/world/SpawnLocation.java
package com.natzsxn.mysmp.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class SpawnLocation {
    private final String worldName;
    private final double x, y, z;
    private final float yaw, pitch;

    public SpawnLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
        this.worldName = worldName;
        this.x = x; this.y = y; this.z = z;
        this.yaw = yaw; this.pitch = pitch;
    }

    public static SpawnLocation from(Location loc) {
        return new SpawnLocation(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    public Location toLocation() {
        World w = Bukkit.getWorld(worldName);
        if (w == null) w = Bukkit.createWorld(new org.bukkit.WorldCreator(worldName));
        return new Location(w, x, y, z, yaw, pitch);
    }

    public String getWorldName() { return worldName; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
}
