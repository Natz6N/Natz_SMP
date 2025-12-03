package com.natzdev.natzsmp.storage.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface LevelDao {
    void ensureRow(Connection conn, int playerId) throws SQLException;
    int getLevel(Connection conn, int playerId) throws SQLException;
    int getXp(Connection conn, int playerId) throws SQLException;
    void setLevel(Connection conn, int playerId, int level) throws SQLException;
    void setXp(Connection conn, int playerId, int xp) throws SQLException;
    Integer getRequiredXpForLevel(Connection conn, int nextLevelNumber) throws SQLException; // returns null if not defined
}
