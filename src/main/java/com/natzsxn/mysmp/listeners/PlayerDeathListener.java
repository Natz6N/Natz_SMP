// FILE: src/main/java/com/natzsxn/mysmp/listeners/PlayerDeathListener.java
package com.natzsxn.mysmp.listeners;

import com.natzsxn.mysmp.bootstrap.ServiceLocator;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private final Messager messager;

    public PlayerDeathListener(ServiceLocator services) {
        this.messager = services.get(Messager.class);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.getEntity().sendMessage(messager.warn("You died. Use /lobby or /survival"));
    }
}
