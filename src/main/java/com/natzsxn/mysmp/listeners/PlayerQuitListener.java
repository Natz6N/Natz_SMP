package com.natzsxn.mysmp.listeners;

import com.natzsxn.mysmp.storage.DatabaseManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final DatabaseManager databaseManager;

    public PlayerQuitListener(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        databaseManager.saveLastLocation(e.getPlayer().getUniqueId(), e.getPlayer().getLocation());
    }
}
