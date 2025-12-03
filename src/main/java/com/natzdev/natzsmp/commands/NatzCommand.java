package com.natzdev.natzsmp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NatzCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("natz.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " <holo|npc|econ|level>");
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "holo":
                return HologramCommand.handle(sender, label, args);
            case "npc":
                sender.sendMessage(ChatColor.GREEN + "NPC command stub.");
                return true;
            case "econ":
                if (sender instanceof Player p) {
                    double bal = com.natzdev.natzsmp.util.ServiceRegistry.get(com.natzdev.natzsmp.api.economy.EconomyService.class).getBalance(p);
                    sender.sendMessage(ChatColor.AQUA + "Balance: " + bal);
                } else sender.sendMessage("Only players.");
                return true;
            case "level":
                if (sender instanceof Player p) {
                    var level = com.natzdev.natzsmp.util.ServiceRegistry.get(com.natzdev.natzsmp.api.level.LevelService.class).getLevel(p);
                    var xp = com.natzdev.natzsmp.util.ServiceRegistry.get(com.natzdev.natzsmp.api.level.LevelService.class).getXp(p);
                    sender.sendMessage(ChatColor.AQUA + "Level: " + level + " XP: " + xp);
                } else sender.sendMessage("Only players.");
                return true;
            default:
                sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " <holo|npc|econ|level>");
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("holo", "npc", "econ", "level");
        }
        if (args.length >= 2 && "holo".equalsIgnoreCase(args[0])) {
            return HologramCommand.tabComplete(sender, args);
        }
        return new ArrayList<>();
    }
}
