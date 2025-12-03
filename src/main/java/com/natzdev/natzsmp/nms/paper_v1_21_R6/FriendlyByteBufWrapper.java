package eu.decentsoftware.holograms.nms.paper_v1_21_R6;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

class FriendlyByteBufWrapper {
   private static final ThreadLocal<FriendlyByteBufWrapper> LOCAL_INSTANCE = ThreadLocal.withInitial(FriendlyByteBufWrapper::new);
   private final FriendlyByteBuf serializer = new FriendlyByteBuf(Unpooled.buffer());

   private FriendlyByteBufWrapper() {
   }

   FriendlyByteBuf getSerializer() {
      return this.serializer;
   }

   void clear() {
      this.serializer.clear();
   }

   void writeIntArray(int[] array) {
      this.serializer.writeVarIntArray(array);
   }

   void writeVarInt(int value) {
      this.serializer.writeVarInt(value);
   }

   int readVarInt() {
      return this.serializer.readVarInt();
   }

   static FriendlyByteBufWrapper getInstance() {
      FriendlyByteBufWrapper instance = (FriendlyByteBufWrapper)LOCAL_INSTANCE.get();
      instance.clear();
      return instance;
   }
}
