// FILE: src/main/java/com/natzsxn/mysmp/listeners/BlockBreakListener.java
package com.natzsxn.mysmp.listeners;

import com.natzsxn.mysmp.bootstrap.ServiceLocator;
import com.natzsxn.mysmp.config.ConfigManager;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private final ConfigManager cfg;
    private final Messager messager;

    public BlockBreakListener(ServiceLocator services) {
        this.cfg = services.get(ConfigManager.class);
        this.messager = services.get(Messager.class);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        String lobbyWorld = cfg.getLobbyWorldName();
        if (e.getBlock().getWorld().getName().equalsIgnoreCase(lobbyWorld)) {
            if (!e.getPlayer().hasPermission("mysmp.lobby.build")) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(messager.warn("Building is disabled in lobby"));
            }
        }
    }
}
