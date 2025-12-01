package com.natzsxn.mysmp.commands;

import com.natzsxn.mysmp.config.ConfigManager;
import com.natzsxn.mysmp.holo.HologramManager;
import com.natzsxn.mysmp.npc.NpcManager;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetLobbyWorldCommand implements CommandExecutor {
    private final ConfigManager configManager;
    private final JavaPlugin plugin;
    private final NpcManager npcManager;
    private final HologramManager holoManager;
    private final Messager messager;

    public SetLobbyWorldCommand(JavaPlugin plugin, ConfigManager configManager, NpcManager npcManager, HologramManager holoManager, Messager messager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.npcManager = npcManager;
        this.holoManager = holoManager;
        this.messager = messager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("mysmp.admin.config")) {
            sender.sendMessage(messager.error("No permission."));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(messager.error("Usage: /setlobbyworld <worldName>"));
            return true;
        }
        String worldName = args[0];
        World w = Bukkit.getWorld(worldName);
        if (w == null) {
            sender.sendMessage(messager.error("World not found: " + worldName));
            return true;
        }
        Bukkit.getLogger().info("MySMP: Updating lobby world -> " + worldName);
        sender.sendMessage(messager.success("Lobby world set: " + worldName));
        // Update config and respawn NPC/holograms
        plugin.getConfig().set("settings.lobby-world", worldName);
        plugin.saveConfig();
        npcManager.reloadAll();
        holoManager.reloadAll();
        return true;
    }
}
