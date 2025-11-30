// FILE: src/main/java/com/natzsxn/mysmp/commands/SetLobbyCommand.java
package com.natzsxn.mysmp.commands;

import com.natzsxn.mysmp.bootstrap.ServiceLocator;
import com.natzsxn.mysmp.storage.WorldDataStorage;
import com.natzsxn.mysmp.util.Messager;
import com.natzsxn.mysmp.world.SpawnLocation;
import com.natzsxn.mysmp.world.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetLobbyCommand implements CommandExecutor {
    private final WorldDataStorage storage;
    private final Messager messager;

    public SetLobbyCommand(ServiceLocator services) {
        this.storage = services.get(WorldDataStorage.class);
        this.messager = services.get(Messager.class);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("mysmp.command.setlobby")) {
            p.sendMessage(messager.error("No permission"));
            return true;
        }
        SpawnLocation spawn = SpawnLocation.from(p.getLocation());
        storage.setSpawn(WorldType.LOBBY, spawn).thenRun(() -> p.sendMessage(messager.success("Lobby spawn set")));
        return true;
    }
}
