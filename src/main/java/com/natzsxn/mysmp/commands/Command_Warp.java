package com.natzsxn.mysmp.commands;

import com.natzsxn.mysmp.service.WarpManager;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Command_Warp - Handles /warp command.
 * Usage: /warp <lobby|survival>
 */
public class Command_Warp implements CommandExecutor, TabCompleter {
    private final WarpManager warpManager;
    private final Messager messager;

    public Command_Warp(WarpManager warpManager, Messager messager) {
        this.warpManager = warpManager;
        this.messager = messager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can warp.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(messager.error("Usage: /warp <lobby|survival>"));
            return true;
        }

        String type = args[0].toLowerCase();
        if (!type.equals("lobby") && !type.equals("survival")) {
            player.sendMessage(messager.error("Unknown warp: " + type));
            return true;
        }

        if (!player.hasPermission("mysmp.warp." + type)) {
            player.sendMessage(messager.error("No permission."));
            return true;
        }

        warpManager.warp(player, type);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if ("lobby".startsWith(args[0].toLowerCase())) completions.add("lobby");
            if ("survival".startsWith(args[0].toLowerCase())) completions.add("survival");
        }
        return completions;
    }
}
