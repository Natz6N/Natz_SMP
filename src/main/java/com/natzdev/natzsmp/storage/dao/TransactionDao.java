package com.natzdev.natzsmp.storage.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface TransactionDao {
    void insert(Connection conn, String type, Integer fromPlayerId, Integer toPlayerId, long amountCents, String note) throws SQLException;
}
