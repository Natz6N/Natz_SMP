package com.natzsxn.mysmp.commands;

import com.natzsxn.mysmp.holo.HologramManager;
import com.natzsxn.mysmp.npc.NpcManager;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadNpcCommand implements CommandExecutor {
    private final NpcManager npcManager;
    private final HologramManager holoManager;
    private final Messager messager;

    public ReloadNpcCommand(NpcManager npcManager, HologramManager holoManager, Messager messager) {
        this.npcManager = npcManager;
        this.holoManager = holoManager;
        this.messager = messager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("mysmp.admin.reloadnpc")) {
            sender.sendMessage(messager.error("No permission."));
            return true;
        }
        npcManager.reloadAll();
        holoManager.reloadAll();
        sender.sendMessage(messager.success("NPC & Hologram respawned."));
        return true;
    }
}
