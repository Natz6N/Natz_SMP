package eu.decentsoftware.holograms.nms.paper_v1_21_R6;

import eu.decentsoftware.holograms.nms.api.renderer.NmsClickableHologramRenderer;
import eu.decentsoftware.holograms.shared.DecentPosition;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

class ClickableHologramRenderer implements NmsClickableHologramRenderer {
   private final int entityId;

   ClickableHologramRenderer(EntityIdGenerator entityIdGenerator) {
      this.entityId = entityIdGenerator.getFreeEntityId();
   }

   public void display(Player player, DecentPosition position) {
      EntityPacketsBuilder.create().withSpawnEntity(this.entityId, EntityType.ARMOR_STAND, position).withEntityMetadata(this.entityId, EntityMetadataBuilder.create().withInvisible().withNoGravity().withArmorStandProperties(false, false).toWatchableObjects()).sendTo(player);
   }

   public void move(Player player, DecentPosition position) {
      EntityPacketsBuilder.create().withTeleportEntity(this.entityId, position).sendTo(player);
   }

   public void hide(Player player) {
      EntityPacketsBuilder.create().withRemoveEntity(this.entityId).sendTo(player);
   }

   public int getEntityId() {
      return this.entityId;
   }
}
