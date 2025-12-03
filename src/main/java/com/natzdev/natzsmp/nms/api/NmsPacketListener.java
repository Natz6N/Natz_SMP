package com.natzdev.natzsmp.nms.api;

import com.natzdev.natzsmp.nms.api.event.NmsEntityInteractEvent;

public interface NmsPacketListener {
   void onEntityInteract(NmsEntityInteractEvent event);
}
