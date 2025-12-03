package eu.decentsoftware.holograms.nms.v1_21_R6;

import eu.decentsoftware.holograms.nms.api.NmsHologramPartData;
import eu.decentsoftware.holograms.nms.api.renderer.NmsEntityHologramRenderer;
import eu.decentsoftware.holograms.shared.DecentPosition;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

class EntityHologramRenderer implements NmsEntityHologramRenderer {
   private final int entityId;

   EntityHologramRenderer(EntityIdGenerator entityIdGenerator) {
      this.entityId = entityIdGenerator.getFreeEntityId();
   }

   public void display(Player player, NmsHologramPartData<EntityType> data) {
      DecentPosition position = data.getPosition();
      EntityType content = (EntityType)data.getContent();
      DecentPosition offsetPosition = this.offsetPosition(position);
      EntityPacketsBuilder.create().withSpawnEntity(this.entityId, content, offsetPosition).withEntityMetadata(this.entityId, EntityMetadataBuilder.create().withSilent().withNoGravity().toWatchableObjects()).sendTo(player);
   }

   public void updateContent(Player player, NmsHologramPartData<EntityType> data) {
      this.hide(player);
      this.display(player, data);
   }

   public void move(Player player, NmsHologramPartData<EntityType> data) {
      this.hide(player);
      this.display(player, data);
   }

   public void hide(Player player) {
      EntityPacketsBuilder.create().withRemoveEntity(this.entityId).sendTo(player);
   }

   public double getHeight(NmsHologramPartData<EntityType> data) {
      return EntityTypeRegistry.getEntityTypeHeight((EntityType)data.getContent());
   }

   public int[] getEntityIds() {
      return new int[]{this.entityId};
   }

   private DecentPosition offsetPosition(DecentPosition position) {
      return position.subtractY(0.25D);
   }
}
