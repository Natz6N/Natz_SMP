// FILE: src/main/java/com/natzsxn/mysmp/service/AgreementService.java
package com.natzsxn.mysmp.service;

import com.natzsxn.mysmp.storage.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AgreementService {
    private final DatabaseManager db;

    public AgreementService(DatabaseManager db) { this.db = db; }

    public CompletableFuture<Boolean> isAgreed(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection c = db.getConnection();
                 PreparedStatement ps = c.prepareStatement("SELECT agreed FROM agreements WHERE player_id = ?")) {
                ps.setString(1, playerId.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1) == 1;
                }
            } catch (Exception e) { db.logError("isAgreed", e); }
            return false;
        });
    }

    public CompletableFuture<Void> setAgreed(UUID playerId, boolean agreed) {
        return CompletableFuture.runAsync(() -> {
            try (Connection c = db.getConnection();
                 PreparedStatement ps = c.prepareStatement("INSERT INTO agreements(player_id, agreed) VALUES(?,?) ON CONFLICT(player_id) DO UPDATE SET agreed=excluded.agreed")) {
                ps.setString(1, playerId.toString());
                ps.setInt(2, agreed ? 1 : 0);
                ps.executeUpdate();
            } catch (Exception e) { db.logError("setAgreed", e); }
        });
    }
}
