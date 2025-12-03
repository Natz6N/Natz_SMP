package com.natzdev.natzsmp.nms.paper21;

import com.natzdev.natzsmp.nms.api.renderer.NmsHologramRendererFactory;
import com.natzdev.natzsmp.nms.api.renderer.NmsTextHologramRenderer;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperHologramRendererFactory implements NmsHologramRendererFactory {
    private final JavaPlugin plugin;

    public PaperHologramRendererFactory(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public NmsTextHologramRenderer createTextRenderer() {
        return new PaperTextHologramRenderer(plugin);
    }
}
