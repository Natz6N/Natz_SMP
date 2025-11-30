// FILE: src/main/java/com/natzsxn/mysmp/listeners/WorldChangeListener.java
package com.natzsxn.mysmp.listeners;

import com.natzsxn.mysmp.bootstrap.ServiceLocator;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldChangeListener implements Listener {
    private final Messager messager;

    public WorldChangeListener(ServiceLocator services) {
        this.messager = services.get(Messager.class);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        messager.send(e.getPlayer(), "Entered world: " + e.getPlayer().getWorld().getName());
    }
}
