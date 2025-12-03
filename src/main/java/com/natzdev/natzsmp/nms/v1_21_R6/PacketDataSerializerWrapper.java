package eu.decentsoftware.holograms.nms.v1_21_R6;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketDataSerializer;

class PacketDataSerializerWrapper {
   private static final ThreadLocal<PacketDataSerializerWrapper> LOCAL_INSTANCE = ThreadLocal.withInitial(PacketDataSerializerWrapper::new);
   private final PacketDataSerializer serializer = new PacketDataSerializer(Unpooled.buffer());

   private PacketDataSerializerWrapper() {
   }

   PacketDataSerializer getSerializer() {
      return this.serializer;
   }

   void clear() {
      this.serializer.clear();
   }

   void writeIntArray(int[] array) {
      this.serializer.a(array);
   }

   void writeVarInt(int value) {
      this.serializer.c(value);
   }

   int readVarInt() {
      return this.serializer.l();
   }

   static PacketDataSerializerWrapper getInstance() {
      PacketDataSerializerWrapper instance = (PacketDataSerializerWrapper)LOCAL_INSTANCE.get();
      instance.clear();
      return instance;
   }
}
