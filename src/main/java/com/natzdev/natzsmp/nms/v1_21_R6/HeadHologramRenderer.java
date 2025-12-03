package eu.decentsoftware.holograms.nms.v1_21_R6;

import eu.decentsoftware.holograms.nms.api.NmsHologramPartData;
import eu.decentsoftware.holograms.nms.api.renderer.NmsHeadHologramRenderer;
import eu.decentsoftware.holograms.shared.DecentPosition;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class HeadHologramRenderer implements NmsHeadHologramRenderer {
   private final int entityId;
   private final boolean small;

   HeadHologramRenderer(EntityIdGenerator entityIdGenerator) {
      this(entityIdGenerator, false);
   }

   protected HeadHologramRenderer(EntityIdGenerator entityIdGenerator, boolean small) {
      this.entityId = entityIdGenerator.getFreeEntityId();
      this.small = small;
   }

   public void display(Player player, NmsHologramPartData<ItemStack> data) {
      DecentPosition position = data.getPosition();
      ItemStack content = (ItemStack)data.getContent();
      DecentPosition offsetPosition = this.offsetPosition(position);
      EntityPacketsBuilder.create().withSpawnEntity(this.entityId, EntityType.ARMOR_STAND, offsetPosition).withEntityMetadata(this.entityId, EntityMetadataBuilder.create().withInvisible().withNoGravity().withArmorStandProperties(this.small, true).toWatchableObjects()).withHelmet(this.entityId, content).sendTo(player);
   }

   public void updateContent(Player player, NmsHologramPartData<ItemStack> data) {
      EntityPacketsBuilder.create().withHelmet(this.entityId, (ItemStack)data.getContent()).sendTo(player);
   }

   public void move(Player player, NmsHologramPartData<ItemStack> data) {
      EntityPacketsBuilder.create().withTeleportEntity(this.entityId, this.offsetPosition(data.getPosition())).sendTo(player);
   }

   public void hide(Player player) {
      EntityPacketsBuilder.create().withRemoveEntity(this.entityId).sendTo(player);
   }

   public double getHeight(NmsHologramPartData<ItemStack> data) {
      return this.small ? 0.5D : 0.7D;
   }

   public int[] getEntityIds() {
      return new int[]{this.entityId};
   }

   private DecentPosition offsetPosition(DecentPosition position) {
      double offsetY = this.small ? 1.1875D : 2.0D;
      return position.subtractY(offsetY);
   }
}
