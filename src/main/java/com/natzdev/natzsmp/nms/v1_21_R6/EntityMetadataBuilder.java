package eu.decentsoftware.holograms.nms.v1_21_R6;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.syncher.DataWatcher.Item;
import org.bukkit.craftbukkit.v1_21_R6.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R6.util.CraftChatMessage;
import org.bukkit.inventory.ItemStack;

class EntityMetadataBuilder {
   private final List<Item<?>> watchableObjects = new ArrayList();

   private EntityMetadataBuilder() {
   }

   List<Item<?>> toWatchableObjects() {
      return this.watchableObjects;
   }

   EntityMetadataBuilder withInvisible() {
      this.watchableObjects.add(EntityMetadataType.ENTITY_PROPERTIES.construct((byte)32));
      return this;
   }

   EntityMetadataBuilder withArmorStandProperties(boolean small, boolean marker) {
      byte data = 8;
      if (small) {
         data = (byte)(data | 1);
      }

      if (marker) {
         data = (byte)(data | 16);
      }

      this.watchableObjects.add(EntityMetadataType.ARMOR_STAND_PROPERTIES.construct(data));
      return this;
   }

   EntityMetadataBuilder withCustomName(String customName) {
      IChatBaseComponent iChatBaseComponent = CraftChatMessage.fromStringOrNull(customName);
      Optional<IChatBaseComponent> optionalIChatBaseComponent = Optional.ofNullable(iChatBaseComponent);
      this.watchableObjects.add(EntityMetadataType.ENTITY_CUSTOM_NAME.construct(optionalIChatBaseComponent));
      boolean visible = !Strings.isNullOrEmpty(customName);
      this.watchableObjects.add(EntityMetadataType.ENTITY_CUSTOM_NAME_VISIBLE.construct(visible));
      return this;
   }

   EntityMetadataBuilder withItemStack(ItemStack itemStack) {
      this.watchableObjects.add(EntityMetadataType.ITEM_STACK.construct(CraftItemStack.asNMSCopy(itemStack)));
      return this;
   }

   EntityMetadataBuilder withSilent() {
      this.watchableObjects.add(EntityMetadataType.ENTITY_SILENT.construct(true));
      return this;
   }

   EntityMetadataBuilder withNoGravity() {
      this.watchableObjects.add(EntityMetadataType.ENTITY_HAS_NO_GRAVITY.construct(true));
      return this;
   }

   static EntityMetadataBuilder create() {
      return new EntityMetadataBuilder();
   }
}
