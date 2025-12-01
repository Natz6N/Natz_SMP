package com.natzsxn.mysmp.service;

import com.natzsxn.mysmp.config.ConfigManager;
import com.natzsxn.mysmp.storage.DatabaseManager;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * WarpManager - Handles player teleportation and warp logic with async
 * operations
 * 
 * Features:
 * - Async world loading and teleportation
 * - Cooldown system to prevent spam
 * - Database logging of warp events
 * - Safety checks and error handling
 * - Configurable spawn locations from config.yml
 * 
 * IMPORTANT: This class uses ConfigManager for spawn locations, not WorldConfig
 * Ensure ConfigManager is properly configured with spawn locations
 */
public class WarpManager {
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final DatabaseManager databaseManager;
    private final Messager messager;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final String META_TP_PENDING = "mysmp_teleport_pending";

    /**
     * Constructor - Initializes WarpManager with required dependencies
     * 
     * @param plugin          JavaPlugin instance for scheduling tasks
     * @param configManager   ConfigManager instance for accessing spawn locations
     * @param databaseManager DatabaseManager instance for logging warp events
     * @param messager        Messager instance for sending messages to players
     */
    public WarpManager(JavaPlugin plugin, ConfigManager configManager, DatabaseManager databaseManager,
            Messager messager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.databaseManager = databaseManager;
        this.messager = messager;
    }

    /**
     * Teleport player to a specific warp type (lobby/survival) with full async
     * handling
     * 
     * Process flow:
     * 1. Check cooldown to prevent spam
     * 2. Validate spawn location configuration
     * 3. Async load world if not already loaded
     * 4. Log warp event to database
     * 5. Execute async teleportation
     * 6. Handle success/error responses
     * 
     * @param player Player to teleport
     * @param type   Warp type ("lobby" or "survival")
     */

    public void warp(Player player, String type) {
        if (player.hasMetadata(META_TP_PENDING)) {
            player.sendMessage(messager.warn("Teleport is pending. Please wait."));
            return;
        }

        if (isOnCooldown(player)) {
            player.sendMessage(messager.warn("Please wait before warping again."));
            return;
        }

        messager.info("Player " + player.getName() + " initiating warp to " + type);

        player.setMetadata(META_TP_PENDING, new org.bukkit.metadata.FixedMetadataValue(plugin, true));

        if ("lobby".equalsIgnoreCase(type)) {
            // Simpan lokasi sebelum masuk lobby (preLobby) secara akurat
            com.natzsxn.mysmp.util.LocationUtil.saveLocationToConfig(plugin.getConfig(),
                    "players." + player.getUniqueId() + ".preLobby", player.getLocation());
            plugin.saveConfig();
            messager.info("Saved preLobby location for " + player.getName() + " -> world:" +
                    player.getWorld().getName() + ", x:" + player.getLocation().getX() + ", y:" +
                    player.getLocation().getY() + ", z:" + player.getLocation().getZ() + ", yaw:" +
                    player.getLocation().getYaw() + ", pitch:" + player.getLocation().getPitch());
        }

        ConfigManager.SimpleSpawn spawn = configManager.getSimpleSpawn(type);
        if (spawn == null) {
            player.removeMetadata(META_TP_PENDING, plugin);
            player.sendMessage(messager.error(type + " spawn is not set."));
            return;
        }

        CompletableFuture<Location> targetFuture;
        if ("survival".equalsIgnoreCase(type)) {
            // Coba restore lokasi preLobby jika tersedia
            Location pre = com.natzsxn.mysmp.util.LocationUtil.loadLocationFromConfig(plugin.getConfig(),
                    "players." + player.getUniqueId() + ".preLobby");
            if (pre != null) {
                messager.info("Restoring preLobby location for " + player.getName());
                targetFuture = CompletableFuture.completedFuture(pre);
            } else {
                targetFuture = loadWorldAndGetLocation(spawn);
            }
        } else {
            targetFuture = loadWorldAndGetLocation(spawn);
        }

        targetFuture
                .thenCompose(location -> {
                    if (location == null) {
                        return CompletableFuture.completedFuture(false);
                    }

                    databaseManager.logWarp(player.getUniqueId(), type);
                    databaseManager.saveLastLocation(player.getUniqueId(), player.getLocation());
                    return teleportPlayerAsync(player, location);
                })
                .whenComplete((success, ex) -> {
                    player.removeMetadata(META_TP_PENDING, plugin);
                    if (ex != null) {
                        messager.error("Warp error", ex);
                        player.sendMessage(messager.error("An error occurred while warping."));
                    } else if (Boolean.TRUE.equals(success)) {
                        player.sendMessage(messager.success("Warped to " + type + "!"));
                        setCooldown(player);
                    } else {
                        player.sendMessage(messager.error("Teleport failed or world could not be loaded."));
                    }
                });
    }

    private CompletableFuture<Location> loadWorldAndGetLocation(ConfigManager.SimpleSpawn spawn) {
        CompletableFuture<Location> future = new CompletableFuture<>();

        // Schedule world loading on main thread (Bukkit requires main thread for world
        // operations)
        Bukkit.getScheduler().runTask(plugin, () -> {
            World world = Bukkit.getWorld(spawn.worldName);
            if (world == null) {
                messager.info("Loading world: " + spawn.worldName);
                try {
                    // Attempt to create/load the world using WorldCreator
                    world = Bukkit.createWorld(new WorldCreator(spawn.worldName));
                } catch (Exception e) {
                    messager.error("Failed to load world " + spawn.worldName, e);
                }
            }

            if (world == null) {
                // World loading failed - complete future with null
                future.complete(null);
            } else {
                // Create location with exact coordinates from configuration
                // Respects user requirement: "Fix warp system so world teleport always uses my
                // defined coordinate."
                Location loc = new Location(world, spawn.x, spawn.y, spawn.z, spawn.yaw, spawn.pitch);
                future.complete(loc);
            }
        });

        return future;
    }

    /**
     * Check if player is currently on warp cooldown
     * Players with mysmp.bypass.cooldown permission bypass this check
     * 
     * @param player Player to check
     * @return true if player is on cooldown, false otherwise
     */

    /**
     * Teleport helper that runs the teleport on the main thread and completes a
     * CompletableFuture<Boolean>
     */
    private CompletableFuture<Boolean> teleportPlayerAsync(Player player, Location loc) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                boolean ok = player.teleport(loc);
                future.complete(ok);
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });
        return future;
    }

    private boolean isOnCooldown(Player player) {
        if (!player.hasPermission("mysmp.bypass.cooldown")) {
            long last = cooldowns.getOrDefault(player.getUniqueId(), 0L);
            return System.currentTimeMillis() - last < 3000; // 3 seconds cooldown
        }
        return false;
    }

    /**
     * Set cooldown timestamp for player to prevent warp spam
     * 
     * @param player Player to set cooldown for
     */
    private void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
