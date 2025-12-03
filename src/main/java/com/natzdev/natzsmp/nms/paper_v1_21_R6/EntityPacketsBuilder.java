package eu.decentsoftware.holograms.nms.paper_v1_21_R6;

import com.mojang.datafixers.util.Pair;
import eu.decentsoftware.holograms.shared.DecentPosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData.DataItem;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class EntityPacketsBuilder {
   private final List<Packet<?>> packets = new ArrayList();

   private EntityPacketsBuilder() {
   }

   void sendTo(Player player) {
      Iterator var2 = this.packets.iterator();

      while(var2.hasNext()) {
         Packet<?> packet = (Packet)var2.next();
         this.sendPacket(player, packet);
      }

   }

   EntityPacketsBuilder withSpawnEntity(int entityId, EntityType type, DecentPosition position) {
      ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(entityId, UUID.randomUUID(), position.getX(), position.getY(), position.getZ(), position.getPitch(), position.getYaw(), EntityTypeRegistry.findEntityTypes(type), type == EntityType.ITEM ? 1 : 0, Vec3.ZERO, (double)position.getYaw());
      this.packets.add(packet);
      return this;
   }

   EntityPacketsBuilder withEntityMetadata(int entityId, List<DataItem<?>> items) {
      List<DataValue<?>> cs = new ArrayList();
      Iterator var4 = items.iterator();

      while(var4.hasNext()) {
         DataItem<?> item = (DataItem)var4.next();
         cs.add(item.value());
      }

      ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(entityId, cs);
      this.packets.add(packet);
      return this;
   }

   EntityPacketsBuilder withHelmet(int entityId, ItemStack itemStack) {
      Pair<EquipmentSlot, net.minecraft.world.item.ItemStack> equipmentPair = new Pair(CraftEquipmentSlot.getNMS(org.bukkit.inventory.EquipmentSlot.HEAD), this.itemStackToNms(itemStack));
      ClientboundSetEquipmentPacket packet = new ClientboundSetEquipmentPacket(entityId, Collections.singletonList(equipmentPair));
      this.packets.add(packet);
      return this;
   }

   EntityPacketsBuilder withTeleportEntity(int entityId, DecentPosition position) {
      Vec3 locationVec3 = new Vec3(position.getX(), position.getY(), position.getZ());
      Vec3 zeroVec3 = new Vec3(0.0D, 0.0D, 0.0D);
      ClientboundTeleportEntityPacket packet = new ClientboundTeleportEntityPacket(entityId, new PositionMoveRotation(locationVec3, zeroVec3, position.getYaw(), position.getPitch()), Set.of(), false);
      this.packets.add(packet);
      return this;
   }

   EntityPacketsBuilder withPassenger(int entityId, int passenger) {
      return this.updatePassenger(entityId, passenger);
   }

   EntityPacketsBuilder withRemovePassenger(int entityId) {
      return this.updatePassenger(entityId, -1);
   }

   private EntityPacketsBuilder updatePassenger(int entityId, int... passengers) {
      FriendlyByteBufWrapper serializer = FriendlyByteBufWrapper.getInstance();
      serializer.writeVarInt(entityId);
      serializer.writeIntArray(passengers);
      ClientboundSetPassengersPacket packet = (ClientboundSetPassengersPacket)ClientboundSetPassengersPacket.STREAM_CODEC.decode(serializer.getSerializer());
      this.packets.add(packet);
      return this;
   }

   EntityPacketsBuilder withRemoveEntity(int entityId) {
      ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(new int[]{entityId});
      this.packets.add(packet);
      return this;
   }

   private void sendPacket(Player player, Packet<?> packet) {
      ((CraftPlayer)player).getHandle().connection.send(packet);
   }

   private net.minecraft.world.item.ItemStack itemStackToNms(ItemStack itemStack) {
      return CraftItemStack.asNMSCopy(itemStack);
   }

   static EntityPacketsBuilder create() {
      return new EntityPacketsBuilder();
   }
}
