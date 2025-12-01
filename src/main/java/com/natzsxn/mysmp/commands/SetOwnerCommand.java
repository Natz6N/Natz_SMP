package com.natzsxn.mysmp.commands;

import com.natzsxn.mysmp.config.ConfigManager;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Command admin untuk mengatur owner UUID di config.
 * Usage: /setowner <uuid>
 */
public class SetOwnerCommand implements CommandExecutor {
    private final ConfigManager configManager;
    private final Messager messager;
    private final JavaPlugin plugin;

    public SetOwnerCommand(JavaPlugin plugin, ConfigManager configManager, Messager messager) {
        this.plugin = plugin;
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
        if (!p.hasPermission("mysmp.admin.config")) {
            p.sendMessage(messager.error("No permission."));
            return true;
        }

        if (args.length < 1) {
            p.sendMessage(messager.error("Usage: /setowner <player|uuid>"));
            return true;
        }
        String raw = args[0].trim();
        // Coba sebagai UUID dulu
        try {
            UUID u = UUID.fromString(raw);
            configManager.setOwnerUUID(u.toString());
            plugin.getConfig().set("settings.owner-username", p.getName());
            plugin.saveConfig();
            Bukkit.getLogger().info("[Owner] Updated UUID -> " + u);
            p.sendMessage(messager.success("Owner UUID updated."));
        } catch (IllegalArgumentException ex) {
            // Bukan UUID, perlakukan sebagai nama
            OfflinePlayer off = Bukkit.getOfflinePlayer(raw);
            UUID u = off.getUniqueId();
            configManager.setOwnerUUID(u.toString());
            plugin.getConfig().set("settings.owner-username", raw);
            plugin.saveConfig();
            Bukkit.getLogger().info("[Owner] Updated via name -> " + raw + " UUID=" + u);
            p.sendMessage(messager.success("Owner set to " + raw));
        }
        return true;
    }
}
