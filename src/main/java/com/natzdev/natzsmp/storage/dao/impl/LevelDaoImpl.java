package com.natzdev.natzsmp.storage.dao.impl;

import com.natzdev.natzsmp.storage.DatabaseManager;
import com.natzdev.natzsmp.storage.dao.BaseDao;
import com.natzdev.natzsmp.storage.dao.LevelDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LevelDaoImpl extends BaseDao implements LevelDao {
    public LevelDaoImpl(DatabaseManager db) { super(db); }

    @Override
    public void ensureRow(Connection conn, int playerId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO player_level(player_id, level, xp, total_xp_earned) VALUES (?, 1, 0, 0)\n" +
                "ON CONFLICT(player_id) DO NOTHING")) {
            ps.setInt(1, playerId);
            ps.executeUpdate();
        }
    }

    @Override
    public int getLevel(Connection conn, int playerId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT level FROM player_level WHERE player_id=?")) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return 1;
            }
        }
    }

    @Override
    public int getXp(Connection conn, int playerId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT xp FROM player_level WHERE player_id=?")) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return 0;
            }
        }
    }

    @Override
    public void setLevel(Connection conn, int playerId, int level) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("UPDATE player_level SET level=?, updated_at=strftime('%s','now') WHERE player_id=?")) {
            ps.setInt(1, Math.max(1, level));
            ps.setInt(2, playerId);
            ps.executeUpdate();
        }
    }

    @Override
    public void setXp(Connection conn, int playerId, int xp) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("UPDATE player_level SET xp=?, updated_at=strftime('%s','now') WHERE player_id=?")) {
            ps.setInt(1, Math.max(0, xp));
            ps.setInt(2, playerId);
            ps.executeUpdate();
        }
    }

    @Override
    public Integer getRequiredXpForLevel(Connection conn, int nextLevelNumber) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT required_xp FROM level WHERE level_number=?")) {
            ps.setInt(1, nextLevelNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return null;
            }
        }
    }
}
