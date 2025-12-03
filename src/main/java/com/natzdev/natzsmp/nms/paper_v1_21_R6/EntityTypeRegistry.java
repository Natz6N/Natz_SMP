package eu.decentsoftware.holograms.nms.paper_v1_21_R6;

import eu.decentsoftware.holograms.nms.api.DecentHologramsNmsException;
import java.util.Optional;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;

final class EntityTypeRegistry {
   private EntityTypeRegistry() {
      throw new IllegalStateException("Utility class");
   }

   static double getEntityTypeHeight(EntityType entityType) {
      return (double)findEntityTypes(entityType).getDimensions().height();
   }

   static net.minecraft.world.entity.EntityType<?> findEntityTypes(EntityType entityType) {
      NamespacedKey namespacedKey = getNamespacedKey(entityType);
      String key = namespacedKey.getKey();
      Optional<net.minecraft.world.entity.EntityType<?>> entityTypes = net.minecraft.world.entity.EntityType.byString(key);
      if (entityTypes.isPresent()) {
         return (net.minecraft.world.entity.EntityType)entityTypes.get();
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
