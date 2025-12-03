package com.natzdev.natzsmp.nms.api;

import org.bukkit.Location;
import java.util.function.Supplier;

public class NmsHologramPartData<T> {
   private final Supplier<Location> positionSupplier;
   private final Supplier<T> contentSupplier;

   public NmsHologramPartData(Supplier<Location> positionSupplier, Supplier<T> contentSupplier) {
      this.positionSupplier = positionSupplier;
      this.contentSupplier = contentSupplier;
   }

   public Location getPosition() {
      return this.positionSupplier.get();
   }

   public T getContent() {
      return this.contentSupplier.get();
   }
}
