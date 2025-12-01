// FILE: src/main/java/com/natzsxn/mysmp/Main.java
package com.natzsxn.mysmp;

import com.natzsxn.mysmp.bootstrap.PluginBootstrap;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class untuk plugin MySMP
 * Kelas utama yang di-load oleh Bukkit/Spigot ketika plugin di-enable
 * 
 * Fungsi:
 * - Menangani lifecycle plugin (onEnable/onDisable)
 * - Inisialisasi PluginBootstrap
 * - Validasi konfigurasi plugin.yml
 */
public class Main extends JavaPlugin {
    private PluginBootstrap bootstrap;

    /**
     * Dipanggil ketika plugin di-enable
     * Proses inisialisasi utama plugin
     */
    @Override
    public void onEnable() {
        // Inisialisasi bootstrap system
        bootstrap = new PluginBootstrap(this);
        bootstrap.start();
        getLogger().info("MySMPPlugin enabled");

        // Validasi command yang terdaftar di plugin.yml
        getLogger().info("Data folder: " + getDataFolder().getAbsolutePath());
        if (getDescription().getCommands() != null) {
            getLogger().info("plugin.yml commands: " + getDescription().getCommands().keySet());
            for (String cmd : getDescription().getCommands().keySet()) {
                if (getCommand(cmd) == null) {
                    getLogger().severe("getCommand('" + cmd + "') returned null. Check plugin.yml name.");
                } else {
                    getLogger().info("Command available: " + cmd);
                }
            }
        } else {
            getLogger().severe("No commands section found in plugin.yml");
        }
    }

    /**
     * Dipanggil ketika plugin di-disable
     * Cleanup resources dan shutdown system
     */
    @Override
    public void onDisable() {
        if (bootstrap != null) bootstrap.stop();
        getLogger().info("MySMPPlugin disabled");
    }
}
