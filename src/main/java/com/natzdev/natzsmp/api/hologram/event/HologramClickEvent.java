package com.natzdev.natzsmp.api.hologram.event;

import com.natzdev.natzsmp.api.hologram.ClickType;
import com.natzdev.natzsmp.api.hologram.Hologram;
import com.natzdev.natzsmp.api.hologram.HologramLine;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event yang dipicu ketika pemain mengklik hologram.
 */
public class HologramClickEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Hologram hologram;
    private final HologramLine line;
    private final ClickType clickType;
    private boolean cancelled = false;

    public HologramClickEvent(Player player, Hologram hologram, HologramLine line, ClickType clickType) {
        this.player = player;
        this.hologram = hologram;
        this.line = line;
        this.clickType = clickType;
    }

    public Player getPlayer() {
        return player;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public HologramLine getLine() {
        return line;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
