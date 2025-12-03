package eu.decentsoftware.holograms.nms.paper_v1_21_R6;

import eu.decentsoftware.holograms.shared.reflect.ReflectUtil;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData.DataItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

class EntityMetadataType<T> {
   private static final EntityDataAccessor<Byte> ENTITY_PROPERTIES_OBJECT = (EntityDataAccessor)ReflectUtil.getFieldValue(Entity.class, "DATA_SHARED_FLAGS_ID");
   private static final EntityDataAccessor<Optional<Component>> ENTITY_CUSTOM_NAME_OBJECT = (EntityDataAccessor)ReflectUtil.getFieldValue(Entity.class, "DATA_CUSTOM_NAME");
   private static final EntityDataAccessor<Boolean> ENTITY_CUSTOM_NAME_VISIBLE_OBJECT = (EntityDataAccessor)ReflectUtil.getFieldValue(Entity.class, "DATA_CUSTOM_NAME_VISIBLE");
   private static final EntityDataAccessor<Boolean> ENTITY_SILENT_OBJECT = (EntityDataAccessor)ReflectUtil.getFieldValue(Entity.class, "DATA_SILENT");
   private static final EntityDataAccessor<Boolean> ENTITY_HAS_NO_GRAVITY_OBJECT = (EntityDataAccessor)ReflectUtil.getFieldValue(Entity.class, "DATA_NO_GRAVITY");
   private static final EntityDataAccessor<Byte> ARMOR_STAND_PROPERTIES_OBJECT = (EntityDataAccessor)ReflectUtil.getFieldValue(ArmorStand.class, "DATA_CLIENT_FLAGS");
   private static final EntityDataAccessor<ItemStack> ITEM_STACK_OBJECT = (EntityDataAccessor)ReflectUtil.getFieldValue(ItemEntity.class, "DATA_ITEM");
   static final EntityMetadataType<Byte> ENTITY_PROPERTIES;
   static final EntityMetadataType<Optional<Component>> ENTITY_CUSTOM_NAME;
   static final EntityMetadataType<Boolean> ENTITY_CUSTOM_NAME_VISIBLE;
   static final EntityMetadataType<Boolean> ENTITY_SILENT;
   static final EntityMetadataType<Boolean> ENTITY_HAS_NO_GRAVITY;
   static final EntityMetadataType<Byte> ARMOR_STAND_PROPERTIES;
   static final EntityMetadataType<ItemStack> ITEM_STACK;
   private final EntityDataAccessor<T> EntityDataAccessor;

   private EntityMetadataType(EntityDataAccessor<T> EntityDataAccessor) {
      this.EntityDataAccessor = EntityDataAccessor;
   }

   DataItem<T> construct(T value) {
      return new DataItem(this.EntityDataAccessor, value);
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
