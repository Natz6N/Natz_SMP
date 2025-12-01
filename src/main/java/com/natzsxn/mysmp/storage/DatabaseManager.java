package com.natzsxn.mysmp.storage;

import com.natzsxn.mysmp.util.Messager;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * DatabaseManager - Handles SQLite database operations.
 * Ensures async execution and connection management.
 */
public class DatabaseManager {
    private final JavaPlugin plugin;
    private final Messager messager;
    private final String fileName;
    private Connection connection;
    private final ExecutorService executor;

    /**
     * Constructor
     * @param plugin JavaPlugin instance
     * @param messager Messager instance for logging
     * @param fileName Database file name
     */
    public DatabaseManager(JavaPlugin plugin, Messager messager, String fileName) {
        this.plugin = plugin;
        this.messager = messager;
        this.fileName = fileName;
        this.executor = Executors.newSingleThreadExecutor(); // Ensure sequential DB writes
    }

    /**
     * Initialize database connection and tables
     */
    public void init() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            File dbFile = new File(dataFolder, fileName);
            if (!dbFile.exists()) {
                dbFile.createNewFile();
            }

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            messager.info("Database connected successfully.");

            initTables();
        } catch (Exception e) {
            messager.error("Failed to initialize database", e);
        }
    }

    private void initTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Table for storing user data (last location)
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "last_world TEXT, " +
                    "x DOUBLE, " +
                    "y DOUBLE, " +
                    "z DOUBLE, " +
                    "yaw FLOAT, " +
                    "pitch FLOAT, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // Table for logging warps
            stmt.execute("CREATE TABLE IF NOT EXISTS warp_logs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "uuid TEXT, " +
                    "warp_type TEXT, " +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // Table for agreements
            stmt.execute("CREATE TABLE IF NOT EXISTS agreements (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "agreed BOOLEAN)");
        }
    }

    /**
     * Close database connection
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                messager.info("Database connection closed.");
            }
            executor.shutdown();
        } catch (SQLException e) {
            messager.error("Error closing database", e);
        }
    }

    /**
     * Log a warp event asynchronously
     * @param uuid Player UUID
     * @param type Warp type (LOBBY/SURVIVAL)
     */
    public void logWarp(UUID uuid, String type) {
        CompletableFuture.runAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO warp_logs (uuid, warp_type) VALUES (?, ?)")) {
                ps.setString(1, uuid.toString());
                ps.setString(2, type);
                ps.executeUpdate();
            } catch (SQLException e) {
                messager.error("Failed to log warp", e);
            }
        }, executor);
    }

    /**
     * Save player's last location asynchronously
     * @param uuid Player UUID
     * @param loc Location to save
     */
    public void saveLastLocation(UUID uuid, Location loc) {
        CompletableFuture.runAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (uuid, last_world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT(uuid) DO UPDATE SET " +
                    "last_world=excluded.last_world, " +
                    "x=excluded.x, " +
                    "y=excluded.y, " +
                    "z=excluded.z, " +
                    "yaw=excluded.yaw, " +
                    "pitch=excluded.pitch, " +
                    "updated_at=CURRENT_TIMESTAMP")) {
                ps.setString(1, uuid.toString());
                ps.setString(2, loc.getWorld().getName());
                ps.setDouble(3, loc.getX());
                ps.setDouble(4, loc.getY());
                ps.setDouble(5, loc.getZ());
                ps.setFloat(6, loc.getYaw());
                ps.setFloat(7, loc.getPitch());
                ps.executeUpdate();
            } catch (SQLException e) {
                messager.error("Failed to save last location", e);
            }
        }, executor);
    }

    /**
     * Get player's last known world name asynchronously
     * @param uuid Player UUID
     * @return CompletableFuture containing world name
     */
    public CompletableFuture<String> getLastWorld(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT last_world FROM users WHERE uuid = ?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("last_world");
                    }
                }
            } catch (SQLException e) {
                messager.error("Failed to get last world", e);
            }
            return null;
        }, executor);
    }

    /**
     * Check if player has agreed to rules
     * @param uuid Player UUID
     * @return CompletableFuture boolean
     */
    public CompletableFuture<Boolean> isAgreed(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT agreed FROM agreements WHERE uuid = ?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getBoolean("agreed");
                    }
                }
            } catch (SQLException e) {
                messager.error("Failed to check agreement", e);
            }
            return false;
        }, executor);
    }

    /**
     * Set player agreement status
     * @param uuid Player UUID
     * @param agreed boolean status
     */
    public void setAgreed(UUID uuid, boolean agreed) {
        CompletableFuture.runAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO agreements (uuid, agreed) VALUES (?, ?) " +
                    "ON CONFLICT(uuid) DO UPDATE SET agreed=excluded.agreed")) {
                ps.setString(1, uuid.toString());
                ps.setBoolean(2, agreed);
                ps.executeUpdate();
            } catch (SQLException e) {
                messager.error("Failed to set agreement", e);
            }
        }, executor);
    }
}
