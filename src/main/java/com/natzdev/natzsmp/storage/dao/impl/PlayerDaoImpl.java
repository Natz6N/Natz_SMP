package com.natzdev.natzsmp.storage.dao.impl;

import com.natzdev.natzsmp.storage.DatabaseManager;
import com.natzdev.natzsmp.storage.dao.BaseDao;
import com.natzdev.natzsmp.storage.dao.PlayerDao;
import org.bukkit.entity.Player;

import java.sql.*;

public class PlayerDaoImpl extends BaseDao implements PlayerDao {
    public PlayerDaoImpl(DatabaseManager db) {
        super(db);
    }

    @Override
    public int ensurePlayer(Connection conn, Player player) throws SQLException {
        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        String ip = player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : null;

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO player(uuid, name, first_seen_at, last_seen_at, last_ip)\n" +
                "VALUES (?,?,?,?,?)\n" +
                "ON CONFLICT(uuid) DO UPDATE SET name=excluded.name, last_seen_at=excluded.last_seen_at, last_ip=excluded.last_ip")) {
            ps.setString(1, uuid);
            ps.setString(2, name);
            ps.setLong(3, System.currentTimeMillis() / 1000);
            ps.setLong(4, System.currentTimeMillis() / 1000);
            ps.setString(5, ip);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM player WHERE uuid=?")) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to ensure player row for uuid=" + uuid);
    }

    @Override
    public Integer findIdByUuid(Connection conn, String uuid) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM player WHERE uuid=?")) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return null;
            }
        }
    }
}
