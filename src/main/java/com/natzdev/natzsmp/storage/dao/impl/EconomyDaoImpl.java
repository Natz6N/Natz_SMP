package com.natzdev.natzsmp.storage.dao.impl;

import com.natzdev.natzsmp.storage.DatabaseManager;
import com.natzdev.natzsmp.storage.dao.BaseDao;
import com.natzdev.natzsmp.storage.dao.EconomyDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EconomyDaoImpl extends BaseDao implements EconomyDao {
    public EconomyDaoImpl(DatabaseManager db) { super(db); }

    @Override
    public long getBalanceCents(Connection conn, int playerId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT amount FROM economy_balance WHERE player_id=? AND currency='default'")) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
                return 0L;
            }
        }
    }

    @Override
    public void upsertBalanceCents(Connection conn, int playerId, long newAmountCents) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO economy_balance(player_id, currency, amount, updated_at) VALUES (?, 'default', ?, strftime('%s','now'))\n" +
                "ON CONFLICT(player_id, currency) DO UPDATE SET amount=excluded.amount, updated_at=excluded.updated_at")) {
            ps.setInt(1, playerId);
            ps.setLong(2, newAmountCents);
            ps.executeUpdate();
        }
    }
}
