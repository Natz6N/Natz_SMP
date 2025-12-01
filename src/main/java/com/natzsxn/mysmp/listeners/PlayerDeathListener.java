package com.natzsxn.mysmp.listeners;

import com.natzsxn.mysmp.util.Messager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private final Messager messager;

    public PlayerDeathListener(Messager messager) {
        this.messager = messager;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.getEntity().sendMessage(messager.warn("You died. Use /warp lobby or /warp survival to return."));
    }
}
