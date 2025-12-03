package com.natzdev.natzsmp.nms.api;

import com.natzdev.natzsmp.nms.api.renderer.NmsHologramRendererFactory;
import org.bukkit.entity.Player;

public interface NmsAdapter {
   NmsHologramRendererFactory getHologramComponentFactory();

   void registerPacketListener(Player player, NmsPacketListener listener);

   void unregisterPacketListener(Player player);
}
