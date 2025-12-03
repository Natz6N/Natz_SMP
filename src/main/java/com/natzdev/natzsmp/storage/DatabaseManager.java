package com.natzdev.natzsmp.storage;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

/**
 * Simple SQLite database manager for NatzSMP.
 *
 * Responsibilities:
 * - Create/open the SQLite file in the plugin data folder.
 * - Configure important PRAGMAs (foreign_keys=ON, journal_mode=WAL).
 * - Apply SQL migrations stored under /database/migrations in the JAR.
 *
 * This class does not contain any game logic; services (economy, level, etc.)
 * can request a {@link Connection} and execute their own queries.
 */
public class DatabaseManager {

    private final JavaPlugin plugin;
    private Connection connection;

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Initialize the SQLite connection and run migrations.
     * Safe to call multiple times; subsequent calls are no-ops.
     */
    public synchronized void init() {
        if (this.connection != null) {
            return;
        }
        try {
            // Ensure data folder exists
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                plugin.getLogger().warning("Could not create plugin data folder for database");
            }

            File dbFile = new File(dataFolder, "natzsmp.db");
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();

            this.connection = DriverManager.getConnection(url);

            // Configure important PRAGMAs for consistency and performance
            try (Statement st = this.connection.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON;");
                st.execute("PRAGMA journal_mode = WAL;");
                st.execute("PRAGMA busy_timeout = 5000;");
            }

            runMigrations();
        } catch (SQLException ex) {
            plugin.getLogger().severe("Failed to initialize SQLite database: " + ex.getMessage());
        }
    }

    /**
     * Get the active SQLite connection. init() will be called lazily if needed.
     */
    public synchronized Connection getConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            init();
        }
        return this.connection;
    }

    /**
     * Close the SQLite connection if it is open.
     */
    public synchronized void close() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException ignored) {
            } finally {
                this.connection = null;
            }
        }
    }

    /**
     * Determine current schema version and apply pending migrations.
     *
     * For now we only have V1__init.sql but the mechanism is extensible.
     */
    private void runMigrations() throws SQLException {
        int current = getCurrentVersion();

        // Version 1: initial schema
        if (current < 1) {
            applyMigration(1, "database/migrations/V1__init.sql");
        }

        // Version 2: plugin features expansion
        if (current < 2) {
            applyMigration(2, "database/migrations/V2__plugin_features.sql");
        }

        // Version 3: NPC behavior + interaction logs
        if (current < 3) {
            applyMigration(3, "database/migrations/V3__npc_behavior_and_logs.sql");
        }

        // Version 4: Menu item pricing and uniqueness
        if (current < 4) {
            applyMigration(4, "database/migrations/V4__menu_price_cents.sql");
        }
    }

    /**
     * Read the maximum version from schema_migration.
     * If the table does not exist yet, version 0 is assumed.
     */
    private int getCurrentVersion() {
        if (this.connection == null) {
            return 0;
        }
        try (Statement st = this.connection.createStatement()) {
            try (ResultSet rs = st.executeQuery("SELECT MAX(version) AS v FROM schema_migration")) {
                if (rs.next()) {
                    int v = rs.getInt("v");
                    return rs.wasNull() ? 0 : v;
                }
                return 0;
            }
        } catch (SQLException ex) {
            // Table does not exist yet or other error: treat as version 0
            return 0;
        }
    }

    /**
     * Apply a single migration from a SQL resource on the classpath.
     */
    private void applyMigration(int version, String resourcePath) throws SQLException {
        String sql = loadResource(resourcePath);
        if (sql == null || sql.isEmpty()) {
            plugin.getLogger().warning("Migration file not found or empty: " + resourcePath);
            return;
        }

        try (Statement st = this.connection.createStatement()) {
            // Very simple splitter: statements separated by ';'.
            // The migration files are authored to avoid embedded ';' in literals.
            String[] parts = sql.split(";");
            for (String part : parts) {
                String stmt = part.trim();
                if (stmt.isEmpty()) continue;
                if (stmt.startsWith("--")) continue;
                st.execute(stmt);
            }
        }

        plugin.getLogger().info("Applied DB migration V" + version + " from " + resourcePath);
    }

    /**
     * Load a resource file from the plugin JAR into a single String.
     */
    private String loadResource(String resourcePath) {
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            }
            return sb.toString();
        } catch (IOException ex) {
            plugin.getLogger().severe("Failed to read resource " + resourcePath + ": " + ex.getMessage());
            return null;
        }
    }
}
