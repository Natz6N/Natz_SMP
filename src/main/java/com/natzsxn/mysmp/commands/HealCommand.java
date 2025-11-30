// FILE: src/main/java/com/natzsxn/mysmp/commands/HealCommand.java
package com.natzsxn.mysmp.commands;

import com.natzsxn.mysmp.bootstrap.ServiceLocator;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HealCommand implements CommandExecutor {
    private final Messager messager;

    public HealCommand(ServiceLocator services) {
        this.messager = services.get(Messager.class);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("mysmp.command.heal")) {
            p.sendMessage(messager.error("No permission"));
            return true;
        }
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        p.sendMessage(messager.success("You have been healed"));
        return true;
    }
}
