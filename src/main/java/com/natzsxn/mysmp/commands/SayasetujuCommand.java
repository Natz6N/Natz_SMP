package com.natzsxn.mysmp.commands;

import com.natzsxn.mysmp.storage.DatabaseManager;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SayasetujuCommand implements CommandExecutor {
    private final DatabaseManager databaseManager;
    private final Messager messager;

    public SayasetujuCommand(DatabaseManager databaseManager, Messager messager) {
        this.databaseManager = databaseManager;
        this.messager = messager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        
        // Check if already agreed?
        databaseManager.isAgreed(player.getUniqueId()).thenAccept(agreed -> {
            if (agreed) {
                player.sendMessage(messager.warn("You have already agreed to the rules."));
            } else {
                databaseManager.setAgreed(player.getUniqueId(), true);
                
                // Remove the rules book from player's inventory
                org.bukkit.inventory.ItemStack[] contents = player.getInventory().getContents();
                for (int i = 0; i < contents.length; i++) {
                    if (contents[i] != null && 
                        com.natzsxn.mysmp.util.RulesBookUtil.isRulesBook(
                            (org.bukkit.plugin.java.JavaPlugin) org.bukkit.Bukkit.getPluginManager().getPlugin("MySMPPlugin"), 
                            contents[i])) {
                        player.getInventory().setItem(i, null);
                    }
                }
                
                player.sendMessage(messager.success("Thank you for agreeing to the rules! Have fun!"));
            }
        });
        
        return true;
    }
}
