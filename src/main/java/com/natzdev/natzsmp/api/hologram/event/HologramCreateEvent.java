package com.natzdev.natzsmp.api.hologram.event;

import com.natzdev.natzsmp.api.hologram.Hologram;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event yang dipicu ketika hologram dibuat.
 */
public class HologramCreateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Hologram hologram;

    public HologramCreateEvent(Hologram hologram) {
        this.hologram = hologram;
    }

    public Hologram getHologram() {
        return hologram;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
