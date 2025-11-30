// FILE: src/main/java/com/natzsxn/mysmp/listeners/RulesBookListener.java
package com.natzsxn.mysmp.listeners;

import com.natzsxn.mysmp.bootstrap.ServiceLocator;
import com.natzsxn.mysmp.config.ConfigManager;
import com.natzsxn.mysmp.config.Messages;
import com.natzsxn.mysmp.service.AgreementService;
import com.natzsxn.mysmp.util.RulesBookUtil;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;

public class RulesBookListener implements Listener {
    private final JavaPlugin plugin;
    private final AgreementService agreementService;
    private final Messages messages;
    private final Messager messager;

    public RulesBookListener(JavaPlugin plugin, ServiceLocator services) {
        this.plugin = plugin;
        this.agreementService = new AgreementService(services.get(com.natzsxn.mysmp.storage.DatabaseManager.class));
        this.messages = services.get(ConfigManager.class).getMessagesConfig();
        this.messager = services.get(Messager.class);
    }

    

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        agreementService.isAgreed(p.getUniqueId()).thenAccept(agreed -> {
            if (!agreed) {
                Bukkit.getScheduler().runTask(plugin, () -> giveRulesBook(p));
            }
        });
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack item = e.getItemDrop().getItemStack();
        if (RulesBookUtil.isRulesBook(plugin, item)) {
            e.setCancelled(true);
            Bukkit.getScheduler().runTask(plugin, () -> giveRulesBook(e.getPlayer()));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        ItemStack current = e.getCurrentItem();
        if (RulesBookUtil.isRulesBook(plugin, current)) {
            e.setCancelled(true);
        }
    }

    private void giveRulesBook(Player p) {
        ItemStack book = RulesBookUtil.create(plugin, messages);
        int empty = p.getInventory().firstEmpty();
        if (empty == -1) {
            p.sendMessage(messager.warn("Inventory full. Book will be given later."));
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                int slot = p.getInventory().firstEmpty();
                if (slot != -1) p.getInventory().setItem(slot, book);
            }, 40L);
        } else {
            if (p.getInventory().getItem(0) == null) {
                p.getInventory().setItem(0, book);
            } else {
                p.getInventory().setItem(empty, book);
            }
        }
    }
}
