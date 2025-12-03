package eu.decentsoftware.holograms.nms.v1_21_R6;

import eu.decentsoftware.holograms.nms.api.NmsHologramPartData;
import eu.decentsoftware.holograms.nms.api.renderer.NmsIconHologramRenderer;
import eu.decentsoftware.holograms.shared.DecentPosition;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class IconHologramRenderer implements NmsIconHologramRenderer {
   private final int itemEntityId;
   private final int armorStandEntityId;

   IconHologramRenderer(EntityIdGenerator entityIdGenerator) {
      this.itemEntityId = entityIdGenerator.getFreeEntityId();
      this.armorStandEntityId = entityIdGenerator.getFreeEntityId();
   }

   public void display(Player player, NmsHologramPartData<ItemStack> data) {
      DecentPosition position = data.getPosition();
      ItemStack content = (ItemStack)data.getContent();
      EntityPacketsBuilder.create().withSpawnEntity(this.armorStandEntityId, EntityType.ARMOR_STAND, this.offsetPosition(position)).withEntityMetadata(this.armorStandEntityId, EntityMetadataBuilder.create().withInvisible().withArmorStandProperties(true, true).toWatchableObjects()).withSpawnEntity(this.itemEntityId, EntityType.ITEM, position).withEntityMetadata(this.itemEntityId, EntityMetadataBuilder.create().withItemStack(content).toWatchableObjects()).withTeleportEntity(this.itemEntityId, position).withPassenger(this.armorStandEntityId, this.itemEntityId).sendTo(player);
   }

   public void updateContent(Player player, NmsHologramPartData<ItemStack> data) {
      EntityPacketsBuilder.create().withEntityMetadata(this.itemEntityId, EntityMetadataBuilder.create().withItemStack((ItemStack)data.getContent()).toWatchableObjects()).sendTo(player);
   }

   public void move(Player player, NmsHologramPartData<ItemStack> data) {
      EntityPacketsBuilder.create().withTeleportEntity(this.armorStandEntityId, this.offsetPosition(data.getPosition())).sendTo(player);
   }

   public void hide(Player player) {
      EntityPacketsBuilder.create().withRemovePassenger(this.armorStandEntityId).withRemoveEntity(this.itemEntityId).withRemoveEntity(this.armorStandEntityId).sendTo(player);
   }

   public double getHeight(NmsHologramPartData<ItemStack> data) {
      return 0.5D;
   }

   public int[] getEntityIds() {
      return new int[]{this.armorStandEntityId, this.itemEntityId};
   }

   private DecentPosition offsetPosition(DecentPosition position) {
      return position.subtractY(0.55D);
   }
}
