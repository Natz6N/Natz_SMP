package com.natzdev.natzsmp.api.npc;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface Npc {
    UUID getId();
    String getName();
    Location getLocation();

    void show(Player player);
    void hide(Player player);
}
