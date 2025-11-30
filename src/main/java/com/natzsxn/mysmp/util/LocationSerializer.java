// FILE: src/main/java/com/natzsxn/mysmp/util/LocationSerializer.java
package com.natzsxn.mysmp.util;

import org.bukkit.Location;

public class LocationSerializer {
    public static String serialize(Location loc) {
        return String.join(":",
                loc.getWorld().getName(),
                String.valueOf(loc.getX()),
                String.valueOf(loc.getY()),
                String.valueOf(loc.getZ()),
                String.valueOf(loc.getYaw()),
                String.valueOf(loc.getPitch())
        );
    }

    public static Location deserialize(String s) {
        String[] parts = s.split(":");
        org.bukkit.World w = org.bukkit.Bukkit.getWorld(parts[0]);
        if (w == null) w = org.bukkit.Bukkit.createWorld(new org.bukkit.WorldCreator(parts[0]));
        return new Location(w,
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]),
                Float.parseFloat(parts[4]),
                Float.parseFloat(parts[5])
        );
    }
}
