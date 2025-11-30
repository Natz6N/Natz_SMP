// FILE: src/main/java/com/natzsxn/mysmp/commands/CommandManager.java
package com.natzsxn.mysmp.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandManager {
    private final JavaPlugin plugin;

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void register(String name, CommandExecutor executor) {
        if (plugin.getCommand(name) != null) {
            plugin.getCommand(name).setExecutor(executor);
            plugin.getLogger().info("Registered command: " + name);
        } else {
            plugin.getLogger().severe("Failed to register command '" + name + "': missing in plugin.yml or wrong name");
        }
    }
}
