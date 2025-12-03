package com.natzdev.natzsmp.storage.dao;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;

public interface PlayerDao {
    int ensurePlayer(Connection conn, Player player) throws SQLException;
    Integer findIdByUuid(Connection conn, String uuid) throws SQLException;
}
