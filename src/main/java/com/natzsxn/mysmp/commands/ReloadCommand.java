// FILE: src/main/java/com/natzsxn/mysmp/commands/ReloadCommand.java
package com.natzsxn.mysmp.commands;

import com.natzsxn.mysmp.bootstrap.ServiceLocator;
import com.natzsxn.mysmp.config.ConfigManager;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
    private final ConfigManager configManager;
    private final Messager messager;

    public ReloadCommand(ServiceLocator services) {
        this.configManager = services.get(ConfigManager.class);
        this.messager = services.get(Messager.class);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("mysmp.command.reload")) {
            sender.sendMessage(messager.error("No permission"));
            return true;
        }
        
        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(messager.error("Usage: /" + label + " reload"));
            return true;
        }
        
        try {
            configManager.reloadAll();
            sender.sendMessage(messager.success("All configurations reloaded successfully"));
        } catch (Exception e) {
            sender.sendMessage(messager.error("Failed to reload configurations: " + e.getMessage()));
        }
        return true;
    }
}
