package eu.decentsoftware.holograms.nms.v1_21_R6;

import eu.decentsoftware.holograms.nms.api.DecentHologramsNmsException;
import eu.decentsoftware.holograms.nms.api.NmsPacketListener;
import eu.decentsoftware.holograms.nms.api.event.NmsEntityInteractAction;
import eu.decentsoftware.holograms.nms.api.event.NmsEntityInteractEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
import org.bukkit.entity.Player;

class InboundPacketHandler extends ChannelInboundHandlerAdapter {
   private final Player player;
   private final NmsPacketListener listener;

   InboundPacketHandler(Player player, NmsPacketListener listener) {
      this.player = player;
      this.listener = listener;
   }

   public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
      if (packet instanceof PacketPlayInUseEntity) {
         PacketPlayInUseEntity packetPlayInUseEntity = (PacketPlayInUseEntity)packet;
         PacketDataSerializerWrapper serializer = PacketDataSerializerWrapper.getInstance();
         PacketPlayInUseEntity.a.encode(serializer.getSerializer(), packetPlayInUseEntity);
         int entityId = serializer.readVarInt();
         int actionEnumValueOrdinal = serializer.readVarInt();
         NmsEntityInteractAction action = this.mapActionEnumValueOrdinalToNmsEntityInteractionAction(actionEnumValueOrdinal);
         NmsEntityInteractEvent event = new NmsEntityInteractEvent(this.player, entityId, action);
         this.listener.onEntityInteract(event);
         if (event.isHandled()) {
            return;
         }
      }

      super.channelRead(ctx, packet);
   }

   private NmsEntityInteractAction mapActionEnumValueOrdinalToNmsEntityInteractionAction(int ordinal) {
      NmsEntityInteractAction var10000;
      switch(ordinal) {
      case 0:
      case 2:
         var10000 = this.player.isSneaking() ? NmsEntityInteractAction.SHIFT_RIGHT_CLICK : NmsEntityInteractAction.RIGHT_CLICK;
         break;
      case 1:
         var10000 = this.player.isSneaking() ? NmsEntityInteractAction.SHIFT_LEFT_CLICK : NmsEntityInteractAction.LEFT_CLICK;
         break;
      default:
         throw new DecentHologramsNmsException("Unknown entity use action: " + ordinal);
      }

      return var10000;
   }
}
