// FILE: src/main/java/com/natzsxn/mysmp/config/Messages.java
package com.natzsxn.mysmp.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Messages {
    private final JavaPlugin plugin;
    private String prefix;

    public Messages(JavaPlugin plugin) { this.plugin = plugin; }

    public void load() {
        plugin.saveResource("messages.yml", false);
        FileConfiguration cfg = plugin.getConfig(); // not used; we'll load via dedicated
        org.bukkit.configuration.file.YamlConfiguration y = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(new java.io.File(plugin.getDataFolder(), "messages.yml"));
        prefix = y.getString("prefix", "&a[MySMP] ");
    }

    public String getPrefix() { return prefix; }
}
