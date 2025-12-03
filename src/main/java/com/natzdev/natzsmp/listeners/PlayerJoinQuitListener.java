package com.natzdev.natzsmp.listeners;

import com.natzdev.natzsmp.api.npc.NpcService;
import com.natzdev.natzsmp.util.ServiceRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        ServiceRegistry.get(NpcService.class).spawnFor(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        ServiceRegistry.get(NpcService.class).despawnFor(e.getPlayer());
    }
}
