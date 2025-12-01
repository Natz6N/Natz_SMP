package com.natzsxn.mysmp.listeners;

import com.natzsxn.mysmp.owner.OwnerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;

public class OwnerListener implements Listener {
    private final OwnerManager ownerManager;

    public OwnerListener(OwnerManager ownerManager) {
        this.ownerManager = ownerManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (ownerManager.isOwner(p)) {
            ownerManager.applyOwnerEffects(p);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (ownerManager.isOwner(p)) {
            ownerManager.clearOwnerEffects(p);
        }
    }
}
