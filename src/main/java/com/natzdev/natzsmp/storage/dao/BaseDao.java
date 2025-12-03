package com.natzdev.natzsmp.storage.dao;

import com.natzdev.natzsmp.storage.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class BaseDao {
    protected final DatabaseManager db;

    protected BaseDao(DatabaseManager db) {
        this.db = db;
    }

    protected void bind(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }
}
