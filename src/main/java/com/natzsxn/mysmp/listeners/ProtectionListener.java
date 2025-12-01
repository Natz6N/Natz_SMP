package com.natzsxn.mysmp.listeners;

import com.natzsxn.mysmp.config.ConfigManager;
import com.natzsxn.mysmp.owner.OwnerManager;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * ProtectionListener - Protects the lobby world from modification.
 * Prevents breaking, placing, PvP, and hunger in lobby.
 */
public class ProtectionListener implements Listener {
    private final ConfigManager configManager;
    private final Messager messager;
    private final OwnerManager ownerManager;

    public ProtectionListener(ConfigManager configManager, Messager messager, OwnerManager ownerManager) {
        this.configManager = configManager;
        this.messager = messager;
        this.ownerManager = ownerManager;
    }

    private boolean isLobby(String worldName) {
        return worldName.equalsIgnoreCase(configManager.getLobbyWorldName());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (isLobby(e.getBlock().getWorld().getName())) {
            if (!canBuild(e.getPlayer())) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(messager.warn("You cannot break blocks in the lobby."));
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (isLobby(e.getBlock().getWorld().getName())) {
            if (!canBuild(e.getPlayer())) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(messager.warn("You cannot place blocks in the lobby."));
            }
        }
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent e) {
        if (isLobby(e.getEntity().getWorld().getName())) {
            if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
                if (!e.getDamager().hasPermission("mysmp.lobby.pvp")) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        if (isLobby(e.getEntity().getWorld().getName())) {
            e.setCancelled(true);
            if (e.getEntity() instanceof Player) {
                ((Player) e.getEntity()).setFoodLevel(20);
            }
        }
    }

    private boolean canBuild(Player p) {
        return p.isOp() || p.hasPermission("mysmp.lobby.build") || ownerManager.isOwner(p);
    }
}
