// FILE: src/main/java/com/natzsxn/mysmp/storage/WorldDataStorage.java
package com.natzsxn.mysmp.storage;

import com.natzsxn.mysmp.world.SpawnLocation;
import com.natzsxn.mysmp.world.WorldType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

public class WorldDataStorage {
    private final DatabaseManager db;

    public WorldDataStorage(DatabaseManager db) {
        this.db = db;
    }

    public CompletableFuture<Void> initSchema() {
        return CompletableFuture.runAsync(() -> {
            // schema created in DatabaseManager.init
        });
    }

    public CompletableFuture<SpawnLocation> getSpawn(WorldType type) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection c = db.getConnection();
                 PreparedStatement ps = c.prepareStatement("SELECT world,x,y,z,yaw,pitch FROM worlds WHERE type = ?")) {
                ps.setString(1, type.name());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new SpawnLocation(
                                rs.getString(1),
                                rs.getDouble(2), rs.getDouble(3), rs.getDouble(4),
                                (float) rs.getDouble(5), (float) rs.getDouble(6)
                        );
                    }
                }
            } catch (Exception e) {
                db.logError("getSpawn", e);
            }
            return null;
        });
    }

    public CompletableFuture<Void> setSpawn(WorldType type, SpawnLocation spawn) {
        return CompletableFuture.runAsync(() -> {
            try (Connection c = db.getConnection();
                 PreparedStatement ps = c.prepareStatement("INSERT INTO worlds(name, world, x, y, z, yaw, pitch, type) VALUES(?,?,?,?,?,?,?,?) ON CONFLICT(name) DO UPDATE SET world=excluded.world,x=excluded.x,y=excluded.y,z=excluded.z,yaw=excluded.yaw,pitch=excluded.pitch,type=excluded.type")) {
                ps.setString(1, type.name().toLowerCase());
                ps.setString(2, spawn.getWorldName());
                ps.setDouble(3, spawn.getX());
                ps.setDouble(4, spawn.getY());
                ps.setDouble(5, spawn.getZ());
                ps.setDouble(6, spawn.getYaw());
                ps.setDouble(7, spawn.getPitch());
                ps.setString(8, type.name());
                ps.executeUpdate();
            } catch (Exception e) {
                db.logError("setSpawn", e);
            }
        });
    }
}
