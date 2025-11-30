// FILE: src/main/java/com/natzsxn/mysmp/listeners/LobbyProtectionListener.java
package com.natzsxn.mysmp.listeners;

import com.natzsxn.mysmp.bootstrap.ServiceLocator;
import com.natzsxn.mysmp.config.ConfigManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class LobbyProtectionListener implements Listener {
    private final ConfigManager cfg;

    public LobbyProtectionListener(ServiceLocator services) {
        this.cfg = services.get(ConfigManager.class);
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent e) {
        if (e.getEntity().getWorld().getName().equalsIgnoreCase(cfg.getLobbyWorldName())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        if (e.getEntity().getWorld().getName().equalsIgnoreCase(cfg.getLobbyWorldName())) {
            e.setCancelled(true);
        }
    }
}
