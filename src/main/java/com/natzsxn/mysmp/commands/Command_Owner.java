package com.natzsxn.mysmp.commands;

import com.natzsxn.mysmp.config.ConfigManager;
import com.natzsxn.mysmp.util.Messager;
import com.natzsxn.mysmp.owner.OwnerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Command_Owner - Handles owner-only commands
 * Usage: /owner <kick|ban|blacklist|reload> [player] [reason]
 */
public class Command_Owner implements CommandExecutor, TabCompleter {
    private final ConfigManager configManager;
    private final Messager messager;
    private OwnerManager ownerManager;

    public Command_Owner(ConfigManager configManager, Messager messager) {
        this.configManager = configManager;
        this.messager = messager;
    }

    public void setOwnerManager(OwnerManager ownerManager) {
        this.ownerManager = ownerManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use owner commands.");
            return true;
        }

        Player player = (Player) sender;
        
        // Check if player is owner
        if (ownerManager != null && !ownerManager.isOwner(player)) {
            player.sendMessage(messager.error("You are not the owner!"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(messager.error("Usage: /owner <kick|ban|blacklist|reload|mode> [player] [reason]"));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "kick":
                return handleKick(player, args);
            case "ban":
                return handleBan(player, args);
            case "blacklist":
                return handleBlacklist(player, args);
            case "reload":
                return handleReload(player);
            case "mode":
                return handleMode(player, args);
            default:
                player.sendMessage(messager.error("Unknown command: " + subCommand));
                return true;
        }
    }

    private boolean handleKick(Player owner, String[] args) {
        if (args.length < 2) {
            owner.sendMessage(messager.error("Usage: /owner kick <player> [reason]"));
            return true;
        }

        String targetName = args[1];
        Player target = Bukkit.getPlayer(targetName);
        
        if (target == null) {
            owner.sendMessage(messager.error("Player " + targetName + " is not online!"));
            return true;
        }

        String reason = args.length > 2 ? String.join(" ", args).substring(args[0].length() + args[1].length() + 2) : "Kicked by owner";
        
        target.kickPlayer(messager.error("Kicked by owner: " + reason));
        owner.sendMessage(messager.success("Kicked " + target.getName() + " for: " + reason));
        
        return true;
    }

    private boolean handleBan(Player owner, String[] args) {
        if (args.length < 2) {
            owner.sendMessage(messager.error("Usage: /owner ban <player> [reason]"));
            return true;
        }

        String targetName = args[1];
        String reason = args.length > 2 ? String.join(" ", args).substring(args[0].length() + args[1].length() + 2) : "Banned by owner";
        
        Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(targetName, reason, null, owner.getName());
        
        Player target = Bukkit.getPlayer(targetName);
        if (target != null) {
            target.kickPlayer(messager.error("Banned by owner: " + reason));
        }
        
        owner.sendMessage(messager.success("Banned " + targetName + " for: " + reason));
        return true;
    }

    private boolean handleBlacklist(Player owner, String[] args) {
        if (args.length < 2) {
            owner.sendMessage(messager.error("Usage: /owner blacklist <player> [reason]"));
            return true;
        }

        String targetName = args[1];
        String reason = args.length > 2 ? String.join(" ", args).substring(args[0].length() + args[1].length() + 2) : "Blacklisted by owner";
        
        // Ban both name and IP if possible
        Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(targetName, reason, null, owner.getName());
        
        Player target = Bukkit.getPlayer(targetName);
        if (target != null) {
            String ip = target.getAddress().getAddress().getHostAddress();
            Bukkit.getBanList(org.bukkit.BanList.Type.IP).addBan(ip, reason, null, owner.getName());
            target.kickPlayer(messager.error("Blacklisted by owner: " + reason));
        }
        
        owner.sendMessage(messager.success("Blacklisted " + targetName + " for: " + reason));
        return true;
    }

    private boolean handleReload(Player owner) {
        configManager.loadAll();
        owner.sendMessage(messager.success("Configuration reloaded!"));
        return true;
    }

    private boolean handleMode(Player owner, String[] args) {
        if (args.length < 2) {
            owner.sendMessage(messager.error("Usage: /owner mode <survival|invisible|creative>"));
            return true;
        }

        String mode = args[1].toLowerCase();
        
        switch (mode) {
            case "survival":
                owner.setGameMode(org.bukkit.GameMode.SURVIVAL);
                owner.sendMessage(messager.success("Mode changed to Survival!"));
                break;
            case "invisible":
                owner.setInvisible(true);
                owner.sendMessage(messager.success("You are now invisible!"));
                break;
            case "visible":
                owner.setInvisible(false);
                owner.sendMessage(messager.success("You are now visible!"));
                break;
            case "creative":
                owner.setGameMode(org.bukkit.GameMode.CREATIVE);
                owner.sendMessage(messager.success("Mode changed to Creative!"));
                break;
            default:
                owner.sendMessage(messager.error("Unknown mode: " + mode + ". Use survival, invisible, visible, or creative"));
                return true;
        }
        
        return true;
    }

private boolean isOwner(Player player) {
    String ownerUUIDStr = configManager.getOwnerUUID();
    if (ownerUUIDStr == null || ownerUUIDStr.trim().isEmpty()) {
        Bukkit.getLogger().warning("MySMP: owner-uuid is not configured (settings.owner-uuid)");
        return false;
    }

    ownerUUIDStr = ownerUUIDStr.trim();
    try {
        UUID ownerUUID = UUID.fromString(ownerUUIDStr);
        boolean isOwner = player.getUniqueId().equals(ownerUUID);
        if (!isOwner) {
            // jangan spam pemain dengan debug; log ke console agar lebih aman
            Bukkit.getLogger().info("MySMP: Owner check for player " + player.getName() +
                " uuid=" + player.getUniqueId() + " configOwner=" + ownerUUIDStr + " => " + isOwner);
        }
        return isOwner;
    } catch (IllegalArgumentException ex) {
        Bukkit.getLogger().severe("MySMP: Invalid owner-uuid in config.yml: " + ownerUUIDStr);
        return false;
    }
}

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (!(sender instanceof Player)) return completions;
        Player player = (Player) sender;
        
        if (ownerManager != null && !ownerManager.isOwner(player)) return completions;

        if (args.length == 1) {
            if ("kick".startsWith(args[0].toLowerCase())) completions.add("kick");
            if ("ban".startsWith(args[0].toLowerCase())) completions.add("ban");
            if ("blacklist".startsWith(args[0].toLowerCase())) completions.add("blacklist");
            if ("reload".startsWith(args[0].toLowerCase())) completions.add("reload");
            if ("mode".startsWith(args[0].toLowerCase())) completions.add("mode");
        } else if (args.length == 2) {
            if ("kick".equalsIgnoreCase(args[0]) || "ban".equalsIgnoreCase(args[0]) || "blacklist".equalsIgnoreCase(args[0])) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (online.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(online.getName());
                    }
                }
            } else if ("mode".equalsIgnoreCase(args[0])) {
                if ("survival".startsWith(args[1].toLowerCase())) completions.add("survival");
                if ("invisible".startsWith(args[1].toLowerCase())) completions.add("invisible");
                if ("visible".startsWith(args[1].toLowerCase())) completions.add("visible");
                if ("creative".startsWith(args[1].toLowerCase())) completions.add("creative");
            }
        }
        
        return completions;
    }
}
