// FILE: src/main/java/com/natzsxn/mysmp/commands/SurvivalCommand.java
package com.natzsxn.mysmp.commands;

import com.natzsxn.mysmp.bootstrap.ServiceLocator;
import com.natzsxn.mysmp.service.TeleportService;
import com.natzsxn.mysmp.util.Messager;
import com.natzsxn.mysmp.world.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SurvivalCommand implements CommandExecutor {
    private final TeleportService teleportService;
    private final Messager messager;

    public SurvivalCommand(ServiceLocator services) {
        this.teleportService = services.get(TeleportService.class);
        this.messager = services.get(Messager.class);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("mysmp.command.survival")) {
            p.sendMessage(messager.error("No permission"));
            return true;
        }
        teleportService.teleportTo(p, WorldType.SURVIVAL);
        return true;
    }
}
