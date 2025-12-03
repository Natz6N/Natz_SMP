package com.natzdev.natzsmp.nms.api.renderer;

import com.natzdev.natzsmp.nms.api.NmsHologramPartData;
import org.bukkit.entity.Player;

public interface NmsHologramRenderer<T> {
   void display(Player var1, NmsHologramPartData<T> var2);

   void updateContent(Player var1, NmsHologramPartData<T> var2);

   void move(Player var1, NmsHologramPartData<T> var2);

   void hide(Player var1);

   double getHeight(NmsHologramPartData<T> var1);

   int[] getEntityIds();
}
