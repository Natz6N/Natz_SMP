package com.natzsxn.mysmp.listeners;

import com.natzsxn.mysmp.storage.DatabaseManager;
import com.natzsxn.mysmp.util.Messager;
import com.natzsxn.mysmp.util.RulesBookUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class RulesBookListener implements Listener {
    private final JavaPlugin plugin;
    private final DatabaseManager databaseManager;
    private final Messager messager;

    public RulesBookListener(JavaPlugin plugin, DatabaseManager databaseManager, Messager messager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.messager = messager;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        databaseManager.isAgreed(p.getUniqueId()).thenAccept(agreed -> {
            if (!agreed) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    RulesBookUtil.giveBookIfNeeded(plugin, p, messager);
                });
            }
        });
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (RulesBookUtil.isRulesBook(plugin, e.getItemDrop().getItemStack())) {
            e.setCancelled(true);
            // Hanya batalkan event, jangan berikan buku kembali untuk menghindari duplikasi
            e.getPlayer().sendMessage(messager.warn("You cannot drop the rules book!"));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        // Cegah semua interaksi dengan buku rules termasuk memindahkan, mengambil, atau menukar
        if ((e.getCurrentItem() != null && RulesBookUtil.isRulesBook(plugin, e.getCurrentItem())) ||
            (e.getCursor() != null && RulesBookUtil.isRulesBook(plugin, e.getCursor()))) {
            e.setCancelled(true);
            if (e.getWhoClicked() instanceof Player) {
                ((Player) e.getWhoClicked()).sendMessage(messager.warn("You cannot move or interact with the rules book!"));
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        // Allow reading the rules book but prevent other interactions
        if (e.getItem() != null && RulesBookUtil.isRulesBook(plugin, e.getItem())) {
            // Only allow right-click to read the book
            if (e.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_AIR || 
                e.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
                // Allow reading - don't cancel the event
                return;
            }
            
            // Prevent other interactions like dropping, moving, etc.
            e.setCancelled(true);
            e.getPlayer().sendMessage(messager.warn("You cannot interact with the rules book!"));
        }
    }

    // Logika pemberian buku dipusatkan di RulesBookUtil.giveBookIfNeeded
}
