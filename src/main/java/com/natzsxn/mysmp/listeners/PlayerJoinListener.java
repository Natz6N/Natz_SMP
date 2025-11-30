// FILE: src/main/java/com/natzsxn/mysmp/listeners/PlayerJoinListener.java
package com.natzsxn.mysmp.listeners;

import com.natzsxn.mysmp.bootstrap.ServiceLocator;
import com.natzsxn.mysmp.config.ConfigManager;
import com.natzsxn.mysmp.service.AgreementService;
import com.natzsxn.mysmp.storage.DatabaseManager;
import com.natzsxn.mysmp.storage.WorldDataStorage;
import com.natzsxn.mysmp.util.Messager;
import com.natzsxn.mysmp.util.RulesBookUtil;
import com.natzsxn.mysmp.world.WorldType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerJoinListener implements Listener {
    private final JavaPlugin plugin;
    private final ConfigManager cfg;
    private final WorldDataStorage storage;
    private final Messager messager;
    private final AgreementService agreementService;

    public PlayerJoinListener(JavaPlugin plugin, ServiceLocator services) {
        this.plugin = plugin;
        this.cfg = services.get(ConfigManager.class);
        this.storage = services.get(WorldDataStorage.class);
        this.messager = services.get(Messager.class);
        this.agreementService = new AgreementService(services.get(DatabaseManager.class));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.sendMessage(messager.success("Hello " + p.getName()));

        agreementService.isAgreed(p.getUniqueId()).thenAccept(agreed -> {
            if (!agreed) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    ItemStack book = RulesBookUtil.create(plugin, cfg.getMessagesConfig());
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
                });
            }
        });

        storage.getSpawn(WorldType.LOBBY).thenAccept(spawn -> {
            if (spawn != null && cfg.isTeleportToLobbyOnJoin()) {
                Location loc = spawn.toLocation();
                p.teleportAsync(loc);
            }
        });
    }
}
