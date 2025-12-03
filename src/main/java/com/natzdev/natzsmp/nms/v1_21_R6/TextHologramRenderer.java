package eu.decentsoftware.holograms.nms.v1_21_R6;

import eu.decentsoftware.holograms.nms.api.NmsHologramPartData;
import eu.decentsoftware.holograms.nms.api.renderer.NmsTextHologramRenderer;
import eu.decentsoftware.holograms.shared.DecentPosition;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

class TextHologramRenderer implements NmsTextHologramRenderer {
   private final int armorStandEntityId;

   TextHologramRenderer(EntityIdGenerator entityIdGenerator) {
      this.armorStandEntityId = entityIdGenerator.getFreeEntityId();
   }

   public void display(Player player, NmsHologramPartData<String> data) {
      String content = (String)data.getContent();
      DecentPosition position = data.getPosition();
      EntityPacketsBuilder.create().withSpawnEntity(this.armorStandEntityId, EntityType.ARMOR_STAND, this.offsetPosition(position)).withEntityMetadata(this.armorStandEntityId, EntityMetadataBuilder.create().withInvisible().withNoGravity().withArmorStandProperties(true, true).withCustomName(content).toWatchableObjects()).sendTo(player);
   }

   public void updateContent(Player player, NmsHologramPartData<String> data) {
      EntityPacketsBuilder.create().withEntityMetadata(this.armorStandEntityId, EntityMetadataBuilder.create().withCustomName((String)data.getContent()).toWatchableObjects()).sendTo(player);
   }

   public void move(Player player, NmsHologramPartData<String> data) {
      EntityPacketsBuilder.create().withTeleportEntity(this.armorStandEntityId, this.offsetPosition(data.getPosition())).sendTo(player);
   }

   public void hide(Player player) {
      EntityPacketsBuilder.create().withRemoveEntity(this.armorStandEntityId).sendTo(player);
   }

   public double getHeight(NmsHologramPartData<String> data) {
      return 0.25D;
   }

   public int[] getEntityIds() {
      return new int[]{this.armorStandEntityId};
   }

   private DecentPosition offsetPosition(DecentPosition position) {
      return position.subtractY(0.5D);
   }
}
