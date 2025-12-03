package eu.decentsoftware.holograms.nms.v1_21_R6;

import eu.decentsoftware.holograms.nms.api.DecentHologramsNmsException;
import java.util.Optional;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;

final class EntityTypeRegistry {
   private EntityTypeRegistry() {
      throw new IllegalStateException("Utility class");
   }

   static double getEntityTypeHeight(EntityType entityType) {
      return (double)findEntityTypes(entityType).n().b();
   }

   static EntityTypes<?> findEntityTypes(EntityType entityType) {
      NamespacedKey namespacedKey = getNamespacedKey(entityType);
      String key = namespacedKey.getKey();
      Optional<EntityTypes<?>> entityTypes = EntityTypes.a(key);
      if (entityTypes.isPresent()) {
         return (EntityTypes)entityTypes.get();
      } else {
         throw new DecentHologramsNmsException("Invalid entity type: " + String.valueOf(entityType));
      }
   }

   private static NamespacedKey getNamespacedKey(EntityType entityType) {
      try {
         return entityType.getKey();
      } catch (IllegalStateException var2) {
         throw new DecentHologramsNmsException("Couldn't get key for entity type: " + String.valueOf(entityType));
      }
   }
}
