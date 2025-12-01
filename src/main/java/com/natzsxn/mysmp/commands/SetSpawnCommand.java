package com.natzsxn.mysmp.commands;

import com.natzsxn.mysmp.config.ConfigManager;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command admin untuk set survival spawn ke lokasi pemain saat ini.
 * Usage: /setspawn
 */
public class SetSpawnCommand implements CommandExecutor {
    private final ConfigManager configManager;
    private final Messager messager;

    public SetSpawnCommand(ConfigManager configManager, Messager messager) {
        this.configManager = configManager;
        this.messager = messager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }

        Player p = (Player) sender;
        if (!p.hasPermission("mysmp.admin.setspawn")) {
            p.sendMessage(messager.error("No permission."));
            return true;
        }

        configManager.setSpawn("survival", p.getLocation());
        p.sendMessage(messager.success("Survival spawn updated to your location."));
        return true;
    }
}
