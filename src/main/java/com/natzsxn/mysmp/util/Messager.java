package com.natzsxn.mysmp.util;

import com.natzsxn.mysmp.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Messager - Handles all message formatting and sending.
 * Replaces System.out.println with proper plugin logging.
 */
public class Messager {
    private final JavaPlugin plugin;
    private final String prefix;

    /**
     * Constructor
     * @param plugin JavaPlugin instance for logging
     * @param prefix Chat prefix
     */
    public Messager(JavaPlugin plugin, String prefix) {
        this.plugin = plugin;
        this.prefix = prefix;
    }

    public void send(CommandSender sender, String message) {
        sender.sendMessage(format(message));
    }

    public String success(String msg) { return format("&a" + msg); }
    public String error(String msg) { return format("&c" + msg); }
    public String warn(String msg) { return format("&e" + msg); }
    
    // Fix: Use plugin logger instead of System.out
    public void info(String msg) { plugin.getLogger().info(msg); }
    public void debug(String msg) { plugin.getLogger().info("[DEBUG] " + msg); }
    public void error(String msg, Throwable ex) { 
        plugin.getLogger().severe(msg);
        if (ex != null) {
            ex.printStackTrace();
        }
    }

    private String format(String message) {
        Component c = LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + message);
        return LegacyComponentSerializer.legacySection().serialize(c);
    }
}
