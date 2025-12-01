package com.natzsxn.mysmp.listeners;

import com.natzsxn.mysmp.util.Messager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldChangeListener implements Listener {
    private final Messager messager;

    public WorldChangeListener(Messager messager) {
        this.messager = messager;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        messager.send(e.getPlayer(), "&7Entered world: &f" + e.getPlayer().getWorld().getName());
    }
}
