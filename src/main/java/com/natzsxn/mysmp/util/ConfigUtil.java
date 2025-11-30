// FILE: src/main/java/com/natzsxn/mysmp/util/ConfigUtil.java
package com.natzsxn.mysmp.util;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigUtil {
    public static String getString(FileConfiguration cfg, String path, String def) {
        String v = cfg.getString(path);
        return v != null ? v : def;
    }

    public static int getInt(FileConfiguration cfg, String path, int def) {
        return cfg.getInt(path, def);
    }

    public static boolean getBoolean(FileConfiguration cfg, String path, boolean def) {
        return cfg.getBoolean(path, def);
    }
}
