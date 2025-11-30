// FILE: src/main/java/com/natzsxn/mysmp/service/SqliteUserService.java
package com.natzsxn.mysmp.service;

import com.natzsxn.mysmp.storage.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SqliteUserService implements UserService {
    private final DatabaseManager db;

    public SqliteUserService(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public CompletableFuture<Void> setLastWorld(UUID playerId, String worldName) {
        return CompletableFuture.runAsync(() -> {
            try (Connection c = db.getConnection();
                 PreparedStatement ps = c.prepareStatement("INSERT INTO users(player_id, last_world) VALUES(?,?) ON CONFLICT(player_id) DO UPDATE SET last_world=excluded.last_world")) {
                ps.setString(1, playerId.toString());
                ps.setString(2, worldName);
                ps.executeUpdate();
            } catch (Exception e) {
                db.logError("setLastWorld", e);
            }
        });
    }

    @Override
    public CompletableFuture<String> getLastWorld(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection c = db.getConnection();
                 PreparedStatement ps = c.prepareStatement("SELECT last_world FROM users WHERE player_id = ?")) {
                ps.setString(1, playerId.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString(1);
                }
            } catch (Exception e) {
                db.logError("getLastWorld", e);
            }
            return null;
        });
    }
}
