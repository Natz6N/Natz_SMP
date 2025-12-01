package com.natzsxn.mysmp.commands;

import com.natzsxn.mysmp.config.ConfigManager;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command_SetSpawn - Handles setting spawn locations.
 * Supports /setlobbyspawn and /setsurvivalspawn.
 */
public class Command_SetSpawn implements CommandExecutor {
    private final ConfigManager configManager;
    private final Messager messager;

    public Command_SetSpawn(ConfigManager configManager, Messager messager) {
        this.configManager = configManager;
        this.messager = messager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can set spawn.");
            return true;
        }

        Player player = (Player) sender;
        String cmd = command.getName().toLowerCase();
        String type;

        if (cmd.equals("setlobbyspawn")) {
            type = "lobby";
        } else if (cmd.equals("setsurvivalspawn")) {
            type = "survival";
        } else {
            return false;
        }

        if (!player.hasPermission("mysmp.admin.setspawn")) {
            player.sendMessage(messager.error("No permission."));
            return true;
        }

        configManager.setSpawn(type, player.getLocation());
        player.sendMessage(messager.success(type + " spawn set to your location!"));
        return true;
    }
}
