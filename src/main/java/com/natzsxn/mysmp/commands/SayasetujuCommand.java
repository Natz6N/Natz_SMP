// FILE: src/main/java/com/natzsxn/mysmp/commands/SayasetujuCommand.java
package com.natzsxn.mysmp.commands;

import com.natzsxn.mysmp.bootstrap.ServiceLocator;
import com.natzsxn.mysmp.config.ConfigManager;
import com.natzsxn.mysmp.service.AgreementService;
import com.natzsxn.mysmp.util.RulesBookUtil;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SayasetujuCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final AgreementService agreementService;
    private final Messager messager;

    public SayasetujuCommand(JavaPlugin plugin, ServiceLocator services) {
        this.plugin = plugin;
        this.agreementService = new AgreementService(services.get(com.natzsxn.mysmp.storage.DatabaseManager.class));
        this.messager = services.get(Messager.class);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }
        Player p = (Player) sender;
        removeRulesBook(p);
        agreementService.setAgreed(p.getUniqueId(), true).thenRun(() -> p.sendMessage(messager.success("Thank you. You have agreed to the rules.")));
        return true;
    }

    private void removeRulesBook(Player p) {
        for (int i = 0; i < p.getInventory().getSize(); i++) {
            ItemStack it = p.getInventory().getItem(i);
            if (RulesBookUtil.isRulesBook(plugin, it)) {
                p.getInventory().setItem(i, null);
            }
        }
    }
}
