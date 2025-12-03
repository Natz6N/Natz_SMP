package com.natzdev.natzsmp.nms.api.renderer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface NmsClickableHologramRenderer {
   void display(Player player, Location position);

   void move(Player player, Location position);

   void hide(Player player);

   int getEntityId();
}
