package com.natzdev.natzsmp.api.hologram.event;

import com.natzdev.natzsmp.api.hologram.Hologram;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event yang dipicu ketika hologram dihapus.
 */
public class HologramDeleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Hologram hologram;

    public HologramDeleteEvent(Hologram hologram) {
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
