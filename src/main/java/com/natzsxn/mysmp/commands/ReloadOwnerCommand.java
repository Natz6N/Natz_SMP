package com.natzsxn.mysmp.commands;

import com.natzsxn.mysmp.owner.OwnerManager;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadOwnerCommand implements CommandExecutor {
    private final OwnerManager ownerManager;
    private final Messager messager;

    public ReloadOwnerCommand(OwnerManager ownerManager, Messager messager) {
        this.ownerManager = ownerManager;
        this.messager = messager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("mysmp.admin.config")) {
            sender.sendMessage(messager.error("No permission."));
            return true;
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (ownerManager.isOwner(p)) {
                ownerManager.applyOwnerEffects(p);
            } else {
                ownerManager.clearOwnerEffects(p);
            }
        }
        sender.sendMessage(messager.success("Owner data reloaded and effects reapplied."));
        return true;
    }
}
