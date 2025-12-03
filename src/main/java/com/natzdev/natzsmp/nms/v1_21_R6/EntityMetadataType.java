package eu.decentsoftware.holograms.nms.v1_21_R6;

import eu.decentsoftware.holograms.shared.reflect.ReflectUtil;
import java.util.Optional;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcher.Item;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.item.ItemStack;

class EntityMetadataType<T> {
   private static final DataWatcherObject<Byte> ENTITY_PROPERTIES_OBJECT = (DataWatcherObject)ReflectUtil.getFieldValue(Entity.class, "aA");
   private static final DataWatcherObject<Optional<IChatBaseComponent>> ENTITY_CUSTOM_NAME_OBJECT = (DataWatcherObject)ReflectUtil.getFieldValue(Entity.class, "bm");
   private static final DataWatcherObject<Boolean> ENTITY_CUSTOM_NAME_VISIBLE_OBJECT = (DataWatcherObject)ReflectUtil.getFieldValue(Entity.class, "bn");
   private static final DataWatcherObject<Boolean> ENTITY_SILENT_OBJECT = (DataWatcherObject)ReflectUtil.getFieldValue(Entity.class, "bo");
   private static final DataWatcherObject<Boolean> ENTITY_HAS_NO_GRAVITY_OBJECT = (DataWatcherObject)ReflectUtil.getFieldValue(Entity.class, "bp");
   private static final DataWatcherObject<Byte> ARMOR_STAND_PROPERTIES_OBJECT = (DataWatcherObject)ReflectUtil.getFieldValue(EntityArmorStand.class, "n");
   private static final DataWatcherObject<ItemStack> ITEM_STACK_OBJECT = (DataWatcherObject)ReflectUtil.getFieldValue(EntityItem.class, "c");
   static final EntityMetadataType<Byte> ENTITY_PROPERTIES;
   static final EntityMetadataType<Optional<IChatBaseComponent>> ENTITY_CUSTOM_NAME;
   static final EntityMetadataType<Boolean> ENTITY_CUSTOM_NAME_VISIBLE;
   static final EntityMetadataType<Boolean> ENTITY_SILENT;
   static final EntityMetadataType<Boolean> ENTITY_HAS_NO_GRAVITY;
   static final EntityMetadataType<Byte> ARMOR_STAND_PROPERTIES;
   static final EntityMetadataType<ItemStack> ITEM_STACK;
   private final DataWatcherObject<T> dataWatcherObject;

   private EntityMetadataType(DataWatcherObject<T> dataWatcherObject) {
      this.dataWatcherObject = dataWatcherObject;
   }

   Item<T> construct(T value) {
      return new Item(this.dataWatcherObject, value);
   }

   static {
      ENTITY_PROPERTIES = new EntityMetadataType(ENTITY_PROPERTIES_OBJECT);
      ENTITY_CUSTOM_NAME = new EntityMetadataType(ENTITY_CUSTOM_NAME_OBJECT);
      ENTITY_CUSTOM_NAME_VISIBLE = new EntityMetadataType(ENTITY_CUSTOM_NAME_VISIBLE_OBJECT);
      ENTITY_SILENT = new EntityMetadataType(ENTITY_SILENT_OBJECT);
      ENTITY_HAS_NO_GRAVITY = new EntityMetadataType(ENTITY_HAS_NO_GRAVITY_OBJECT);
      ARMOR_STAND_PROPERTIES = new EntityMetadataType(ARMOR_STAND_PROPERTIES_OBJECT);
      ITEM_STACK = new EntityMetadataType(ITEM_STACK_OBJECT);
   }
}
