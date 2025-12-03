package com.natzdev.natzsmp.nms.paper21;

import com.natzdev.natzsmp.nms.api.NmsHologramPartData;
import com.natzdev.natzsmp.nms.api.renderer.NmsTextHologramRenderer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PaperTextHologramRenderer implements NmsTextHologramRenderer {
    private final JavaPlugin plugin;
    private final Map<UUID, ArmorStand> perPlayer = new HashMap<>();

    public PaperTextHologramRenderer(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void display(Player player, NmsHologramPartData<Component> data) {
        Component content = data.getContent();
        Location pos = data.getPosition();
        if (pos == null || pos.getWorld() == null || !player.isOnline()) return;
        ArmorStand as = spawn(player, pos, content);
        // Hide from others to simulate per-player packet rendering
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getUniqueId().equals(player.getUniqueId())) {
                p.hideEntity(plugin, as);
            }
        }
        perPlayer.put(player.getUniqueId(), as);
    }

    @Override
    public void updateContent(Player player, NmsHologramPartData<Component> data) {
        ArmorStand as = perPlayer.get(player.getUniqueId());
        if (as != null && !as.isDead()) {
            as.customName(data.getContent());
        }
    }

    @Override
    public void move(Player player, NmsHologramPartData<Component> data) {
        ArmorStand as = perPlayer.get(player.getUniqueId());
        Location loc = data.getPosition();
        if (as != null && loc != null) {
            World world = loc.getWorld();
            if (!Objects.equals(world, as.getWorld())) {
                as.remove();
                perPlayer.put(player.getUniqueId(), spawn(player, loc, data.getContent()));
            } else {
                as.teleport(loc.clone());
            }
        }
    }

    @Override
    public void hide(Player player) {
        ArmorStand as = perPlayer.remove(player.getUniqueId());
        if (as != null) {
            as.remove();
        }
    }

    @Override
    public double getHeight(NmsHologramPartData<Component> data) {
        return 0.25D;
    }

    @Override
    public int[] getEntityIds() {
        // Not applicable without CraftBukkit/NMS access; return empty.
        return new int[0];
    }

    private ArmorStand spawn(Player viewer, Location loc, Component text) {
        World world = loc.getWorld();
        return world.spawn(loc, ArmorStand.class, as -> {
            as.setMarker(true);
            as.setInvisible(true);
            as.setGravity(false);
            as.setSmall(true);
            as.setCustomNameVisible(true);
            as.customName(text != null ? text : Component.empty());
            as.setPersistent(false);
        });
    }
}
