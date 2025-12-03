package com.natzdev.natzsmp.storage.dao.impl;

import com.natzdev.natzsmp.storage.DatabaseManager;
import com.natzdev.natzsmp.storage.dao.BaseDao;
import com.natzdev.natzsmp.storage.dao.TransactionDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class TransactionDaoImpl extends BaseDao implements TransactionDao {
    public TransactionDaoImpl(DatabaseManager db) { super(db); }

    @Override
    public void insert(Connection conn, String type, Integer fromPlayerId, Integer toPlayerId, long amountCents, String note) throws SQLException {
        String txId = UUID.randomUUID().toString().replace("-", "");
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO economy_transaction (transaction_id, amount, type, status, created_at, from_player_id, to_player_id, note)\n" +
                "VALUES (?, ?, ?, 'completed', strftime('%s','now'), ?, ?, ?)")) {
            int i = 1;
            ps.setString(i++, txId);
            ps.setLong(i++, amountCents);
            ps.setString(i++, type);
            if (fromPlayerId == null) ps.setNull(i++, java.sql.Types.INTEGER); else ps.setInt(i++, fromPlayerId);
            if (toPlayerId == null) ps.setNull(i++, java.sql.Types.INTEGER); else ps.setInt(i++, toPlayerId);
            ps.setString(i, note);
            ps.executeUpdate();
        }
    }
}
