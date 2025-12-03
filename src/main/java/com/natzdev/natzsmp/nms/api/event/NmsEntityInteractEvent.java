package com.natzdev.natzsmp.nms.api.event;

import org.bukkit.entity.Player;

public class NmsEntityInteractEvent {
   private final Player player;
   private final int entityId;
   private final NmsEntityInteractAction action;
   private boolean handled = false;

   public NmsEntityInteractEvent(Player player, int entityId, NmsEntityInteractAction action) {
      this.player = player;
      this.entityId = entityId;
      this.action = action;
   }

   public Player getPlayer() {
      return this.player;
   }

   public int getEntityId() {
      return this.entityId;
   }

   public NmsEntityInteractAction getAction() {
      return this.action;
   }

   public boolean isHandled() {
      return this.handled;
   }

   public void setHandled(boolean handled) {
      this.handled = handled;
   }
}
