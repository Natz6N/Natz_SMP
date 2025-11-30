// FILE: src/main/java/com/natzsxn/mysmp/storage/DatabaseManager.java
package com.natzsxn.mysmp.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseManager {
    private final JavaPlugin plugin;
    private final String jdbcUrl;
    private HikariDataSource ds;

    public DatabaseManager(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        File dataDir = plugin.getDataFolder();
        if (!dataDir.exists()) dataDir.mkdirs();
        File dbFile = new File(dataDir, fileName);
        this.jdbcUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();
    }

    public void init() {
        try {
            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl(jdbcUrl);
            cfg.setDriverClassName("org.sqlite.JDBC");
            cfg.setMaximumPoolSize(8);
            cfg.setPoolName("MySMP-SQLite");
            cfg.addDataSourceProperty("busy_timeout", "5000");
            ds = new HikariDataSource(cfg);

            try (Connection c = getConnection(); Statement st = c.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS users (player_id TEXT PRIMARY KEY, last_world TEXT)");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS worlds (name TEXT PRIMARY KEY, world TEXT, x REAL, y REAL, z REAL, yaw REAL, pitch REAL, type TEXT)");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS agreements (player_id TEXT PRIMARY KEY, agreed INTEGER)");
            }
            plugin.getLogger().info("Database initialized: " + jdbcUrl);
        } catch (Exception e) {
            logError("init", e);
        }
    }

    public Connection getConnection() throws Exception {
        return ds.getConnection();
    }

    public void shutdown() {
        if (ds != null) ds.close();
    }

    public void logError(String ctx, Exception e) {
        plugin.getLogger().severe("[DB] " + ctx + ": " + e.getMessage());
    }
}
