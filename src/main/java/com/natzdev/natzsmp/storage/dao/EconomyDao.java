package com.natzdev.natzsmp.storage.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface EconomyDao {
    long getBalanceCents(Connection conn, int playerId) throws SQLException;
    void upsertBalanceCents(Connection conn, int playerId, long newAmountCents) throws SQLException;
}
