package eu.decentsoftware.holograms.nms.paper_v1_21_R6;

import eu.decentsoftware.holograms.nms.api.DecentHologramsNmsException;
import eu.decentsoftware.holograms.nms.api.NmsAdapter;
import eu.decentsoftware.holograms.nms.api.NmsPacketListener;
import eu.decentsoftware.holograms.nms.api.renderer.NmsHologramRendererFactory;
import eu.decentsoftware.holograms.shared.reflect.ReflectField;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NmsAdapterImpl implements NmsAdapter {
   private static final String PACKET_HANDLER_NAME = "decent_holograms_packet_handler";
   private static final String DEFAULT_PIPELINE_TAIL = "DefaultChannelPipeline$TailContext#0";
   private static final ReflectField<Connection> NETWORK_MANAGER_FIELD = new ReflectField(ServerCommonPacketListenerImpl.class, "connection");
   private final HologramRendererFactory hologramComponentFactory = new HologramRendererFactory(new EntityIdGenerator());

   public NmsHologramRendererFactory getHologramComponentFactory() {
      return this.hologramComponentFactory;
   }

   public void registerPacketListener(Player player, NmsPacketListener listener) {
      Objects.requireNonNull(player, "player cannot be null");
      Objects.requireNonNull(listener, "listener cannot be null");
      this.executeOnPipelineInEventLoop(player, (pipeline) -> {
         if (pipeline.get("decent_holograms_packet_handler") != null) {
            pipeline.remove("decent_holograms_packet_handler");
         }

         pipeline.addBefore("packet_handler", "decent_holograms_packet_handler", new InboundPacketHandler(player, listener));
      });
   }

   public void unregisterPacketListener(Player player) {
      Objects.requireNonNull(player, "player cannot be null");
      this.executeOnPipelineInEventLoop(player, (pipeline) -> {
         if (pipeline.get("decent_holograms_packet_handler") != null) {
            pipeline.remove("decent_holograms_packet_handler");
         }

      });
   }

   private void executeOnPipelineInEventLoop(Player player, Consumer<ChannelPipeline> task) {
      ChannelPipeline pipeline = this.getPipeline(player);
      EventLoop eventLoop = pipeline.channel().eventLoop();
      if (eventLoop.inEventLoop()) {
         this.executeOnPipeline(player, task, pipeline);
      } else {
         eventLoop.execute(() -> {
            this.executeOnPipeline(player, task, pipeline);
         });
      }

   }

   private ChannelPipeline getPipeline(Player player) {
      ServerGamePacketListenerImpl playerConnection = ((CraftPlayer)player).getHandle().connection;
      Connection networkManager = (Connection)NETWORK_MANAGER_FIELD.get(playerConnection);
      return networkManager.channel.pipeline();
   }

   private void executeOnPipeline(Player player, Consumer<ChannelPipeline> task, ChannelPipeline pipeline) {
      if (player.isOnline()) {
         try {
            task.accept(pipeline);
         } catch (NoSuchElementException var6) {
            List<String> handlers = pipeline.names();
            if (handlers.size() == 1 && ((String)handlers.getFirst()).equals("DefaultChannelPipeline$TailContext#0")) {
               return;
            }

            this.throwFailedToModifyPipelineException(player, var6);
         } catch (Exception var7) {
            this.throwFailedToModifyPipelineException(player, var7);
         }

      }
   }

   private void throwFailedToModifyPipelineException(Player player, Exception e) {
      throw new DecentHologramsNmsException("Failed to modify player's pipeline. player: " + player.getName(), e);
   }
}
