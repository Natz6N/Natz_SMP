package com.natzdev.natzsmp.nms;

import com.natzdev.natzsmp.nms.api.NmsPacketListener;
import com.natzdev.natzsmp.nms.api.renderer.NmsHologramRendererFactory;
import com.natzdev.natzsmp.nms.paper21.PaperHologramRendererFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperAdapter implements com.natzdev.natzsmp.nms.NmsAdapter, com.natzdev.natzsmp.nms.api.NmsAdapter {
    private final JavaPlugin plugin;

    public PaperAdapter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getVersion() {
        return Bukkit.getBukkitVersion();
    }

    @Override
    public NmsHologramRendererFactory getHologramComponentFactory() {
        return new PaperHologramRendererFactory(plugin);
    }

    @Override
    public void registerPacketListener(Player player, NmsPacketListener listener) {
        // Paper-only: no low-level Netty. Could be implemented with Protocolize/PacketEvents if added later.
    }

    @Override
    public void unregisterPacketListener(Player player) {
        // no-op for Paper-only adapter
    }
}
