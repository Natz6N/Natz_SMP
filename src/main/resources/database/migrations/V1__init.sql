-- V1__init: Initial SQLite schema for NatzSMP
-- Note: PRAGMA foreign_keys = ON and journal_mode = WAL are configured by the plugin at runtime.

CREATE TABLE IF NOT EXISTS player (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    uuid            TEXT    NOT NULL UNIQUE,
    name            TEXT    NOT NULL,
    first_seen_at   INTEGER NOT NULL,
    last_seen_at    INTEGER NOT NULL,
    last_ip         TEXT,
    data_version    INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS player_economy (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id           INTEGER NOT NULL,
    balance             REAL    NOT NULL DEFAULT 0,
    total_earned        REAL    NOT NULL DEFAULT 0,
    total_spent         REAL    NOT NULL DEFAULT 0,
    last_transaction_at INTEGER,
    FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_player_economy_player
    ON player_economy(player_id);

CREATE TABLE IF NOT EXISTS economy_transaction (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id   INTEGER NOT NULL,
    amount      REAL    NOT NULL,
    reason      TEXT    NOT NULL,
    source      TEXT,
    created_at  INTEGER NOT NULL,
    FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_econ_tx_player_time
    ON economy_transaction(player_id, created_at DESC);

CREATE TABLE IF NOT EXISTS player_level (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id        INTEGER NOT NULL,
    level            INTEGER NOT NULL DEFAULT 1,
    xp               INTEGER NOT NULL DEFAULT 0,
    total_xp_earned  INTEGER NOT NULL DEFAULT 0,
    last_level_up_at INTEGER,
    FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_player_level_player
    ON player_level(player_id);

CREATE TABLE IF NOT EXISTS npc (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        TEXT    NOT NULL UNIQUE,
    world       TEXT    NOT NULL,
    x           REAL    NOT NULL,
    y           REAL    NOT NULL,
    z           REAL    NOT NULL,
    yaw         REAL    NOT NULL,
    pitch       REAL    NOT NULL,
    skin_profile TEXT,
    enabled     INTEGER NOT NULL DEFAULT 1,
    created_at  INTEGER NOT NULL,
    updated_at  INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS npc_action (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    npc_id    INTEGER NOT NULL,
    slot      INTEGER NOT NULL,
    type      TEXT    NOT NULL,
    payload   TEXT    NOT NULL,
    FOREIGN KEY (npc_id) REFERENCES npc(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_npc_action_npc_slot
    ON npc_action(npc_id, slot);

CREATE TABLE IF NOT EXISTS menu (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    name           TEXT    NOT NULL UNIQUE,
    title          TEXT    NOT NULL,
    rows           INTEGER NOT NULL,
    permission     TEXT,
    config_version INTEGER NOT NULL DEFAULT 1,
    created_at     INTEGER NOT NULL,
    updated_at     INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS menu_item (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    menu_id       INTEGER NOT NULL,
    slot          INTEGER NOT NULL,
    material      TEXT    NOT NULL,
    amount        INTEGER NOT NULL DEFAULT 1,
    display_name  TEXT,
    lore          TEXT,
    action_type   TEXT,
    action_payload TEXT,
    enabled       INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (menu_id) REFERENCES menu(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_menu_item_menu_slot
    ON menu_item(menu_id, slot);

CREATE TABLE IF NOT EXISTS player_data (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id  INTEGER NOT NULL,
    key        TEXT    NOT NULL,
    value      TEXT    NOT NULL,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_player_data_key
    ON player_data(player_id, key);

CREATE TABLE IF NOT EXISTS command_log (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id   INTEGER,
    command     TEXT    NOT NULL,
    arguments   TEXT,
    is_console  INTEGER NOT NULL DEFAULT 0,
    success     INTEGER NOT NULL DEFAULT 1,
    created_at  INTEGER NOT NULL,
    FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_command_log_time
    ON command_log(created_at DESC);

CREATE INDEX IF NOT EXISTS idx_command_log_player
    ON command_log(player_id, created_at DESC);

CREATE TABLE IF NOT EXISTS config (
    key        TEXT PRIMARY KEY,
    value      TEXT    NOT NULL,
    updated_at INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS schema_migration (
    version     INTEGER PRIMARY KEY,
    description TEXT    NOT NULL,
    applied_at  INTEGER NOT NULL
);

INSERT INTO schema_migration(version, description, applied_at)
SELECT 1, 'Initial schema', strftime('%s','now')
WHERE NOT EXISTS (SELECT 1 FROM schema_migration WHERE version = 1);
