package eu.decentsoftware.holograms.nms.v1_21_R6;

import com.mojang.datafixers.util.Pair;
import eu.decentsoftware.holograms.shared.DecentPosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher.Item;
import net.minecraft.network.syncher.DataWatcher.c;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.craftbukkit.v1_21_R6.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_21_R6.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R6.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
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
      PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(entityId, UUID.randomUUID(), position.getX(), position.getY(), position.getZ(), position.getPitch(), position.getYaw(), EntityTypeRegistry.findEntityTypes(type), type == EntityType.ITEM ? 1 : 0, Vec3D.c, (double)position.getYaw());
      this.packets.add(packet);
      return this;
   }

   EntityPacketsBuilder withEntityMetadata(int entityId, List<Item<?>> items) {
      List<c<?>> cs = new ArrayList();
      Iterator var4 = items.iterator();

      while(var4.hasNext()) {
         Item<?> item = (Item)var4.next();
         cs.add(item.e());
      }

      PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(entityId, cs);
      this.packets.add(packet);
      return this;
   }

   EntityPacketsBuilder withHelmet(int entityId, ItemStack itemStack) {
      Pair<EnumItemSlot, net.minecraft.world.item.ItemStack> equipmentPair = new Pair(CraftEquipmentSlot.getNMS(EquipmentSlot.HEAD), this.itemStackToNms(itemStack));
      PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(entityId, Collections.singletonList(equipmentPair));
      this.packets.add(packet);
      return this;
   }

   EntityPacketsBuilder withTeleportEntity(int entityId, DecentPosition position) {
      Vec3D locationVec3D = new Vec3D(position.getX(), position.getY(), position.getZ());
      Vec3D zeroVec3D = new Vec3D(0.0D, 0.0D, 0.0D);
      PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(entityId, new PositionMoveRotation(locationVec3D, zeroVec3D, position.getYaw(), position.getPitch()), Set.of(), false);
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
      PacketDataSerializerWrapper serializer = PacketDataSerializerWrapper.getInstance();
      serializer.writeVarInt(entityId);
      serializer.writeIntArray(passengers);
      PacketPlayOutMount packet = (PacketPlayOutMount)PacketPlayOutMount.a.decode(serializer.getSerializer());
      this.packets.add(packet);
      return this;
   }

   EntityPacketsBuilder withRemoveEntity(int entityId) {
      PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[]{entityId});
      this.packets.add(packet);
      return this;
   }

   private void sendPacket(Player player, Packet<?> packet) {
      ((CraftPlayer)player).getHandle().g.b(packet);
   }

   private net.minecraft.world.item.ItemStack itemStackToNms(ItemStack itemStack) {
      return CraftItemStack.asNMSCopy(itemStack);
   }

   static EntityPacketsBuilder create() {
      return new EntityPacketsBuilder();
   }
}
