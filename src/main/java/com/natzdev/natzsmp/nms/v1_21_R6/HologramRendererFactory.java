package eu.decentsoftware.holograms.nms.v1_21_R6;

import eu.decentsoftware.holograms.nms.api.renderer.NmsClickableHologramRenderer;
import eu.decentsoftware.holograms.nms.api.renderer.NmsEntityHologramRenderer;
import eu.decentsoftware.holograms.nms.api.renderer.NmsHeadHologramRenderer;
import eu.decentsoftware.holograms.nms.api.renderer.NmsHologramRendererFactory;
import eu.decentsoftware.holograms.nms.api.renderer.NmsIconHologramRenderer;
import eu.decentsoftware.holograms.nms.api.renderer.NmsSmallHeadHologramRenderer;
import eu.decentsoftware.holograms.nms.api.renderer.NmsTextHologramRenderer;

class HologramRendererFactory implements NmsHologramRendererFactory {
   private final EntityIdGenerator entityIdGenerator;

   HologramRendererFactory(EntityIdGenerator entityIdGenerator) {
      this.entityIdGenerator = entityIdGenerator;
   }

   public NmsTextHologramRenderer createTextRenderer() {
      return new TextHologramRenderer(this.entityIdGenerator);
   }

   public NmsIconHologramRenderer createIconRenderer() {
      return new IconHologramRenderer(this.entityIdGenerator);
   }

   public NmsHeadHologramRenderer createHeadRenderer() {
      return new HeadHologramRenderer(this.entityIdGenerator);
   }

   public NmsSmallHeadHologramRenderer createSmallHeadRenderer() {
      return new SmallHeadHologramRenderer(this.entityIdGenerator);
   }

   public NmsEntityHologramRenderer createEntityRenderer() {
      return new EntityHologramRenderer(this.entityIdGenerator);
   }

   public NmsClickableHologramRenderer createClickableRenderer() {
      return new ClickableHologramRenderer(this.entityIdGenerator);
   }
}
