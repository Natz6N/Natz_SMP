package eu.decentsoftware.holograms.nms.paper_v1_21_R6;

import eu.decentsoftware.holograms.nms.api.DecentHologramsNmsException;
import eu.decentsoftware.holograms.shared.reflect.ReflectField;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.world.entity.Entity;

class EntityIdGenerator {
   private static final ReflectField<AtomicInteger> ENTITY_COUNT_FIELD = new ReflectField(Entity.class, "ENTITY_COUNTER");

   int getFreeEntityId() {
      try {
         AtomicInteger entityCount = (AtomicInteger)ENTITY_COUNT_FIELD.get((Object)null);
         return entityCount.incrementAndGet();
      } catch (Exception var2) {
         throw new DecentHologramsNmsException("Failed to get new entity ID", var2);
      }
   }
}
