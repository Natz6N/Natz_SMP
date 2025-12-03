-- V2__plugin_features: Align DB with full plugin features
-- This migration is designed to be idempotent and preserve data where possible.
-- Notes:
-- - Keep INTEGER PRIMARY KEY AUTOINCREMENT for PKs.
-- - Introduce UUID TEXT transaction_id for economy_transaction.
-- - Convert monetary values from REAL to INTEGER smallest units (x100) during migration.
-- - Use explicit ON DELETE behaviors: CASCADE for owned data; SET NULL for audit/history.

BEGIN;

-- Player profile (owned by player)
CREATE TABLE IF NOT EXISTS player_profile (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  player_id     INTEGER NOT NULL,
  display_name  TEXT,
  country       TEXT,
  language      TEXT,
  metadata      TEXT,
  created_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  updated_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  UNIQUE (player_id),
  FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_player_profile_player ON player_profile (player_id);

-- Economy balance (per player, per currency)
CREATE TABLE IF NOT EXISTS economy_balance (
  id         INTEGER PRIMARY KEY AUTOINCREMENT,
  player_id  INTEGER NOT NULL,
  currency   TEXT    NOT NULL DEFAULT 'default',
  amount     INTEGER NOT NULL DEFAULT 0 CHECK (amount >= 0),
  updated_at INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  UNIQUE (player_id, currency),
  FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_econ_balance_player ON economy_balance (player_id);
CREATE INDEX IF NOT EXISTS idx_econ_balance_currency ON economy_balance (currency);

-- Seed economy_balance from legacy player_economy if present
INSERT OR IGNORE INTO economy_balance (player_id, currency, amount, updated_at)
SELECT pe.player_id, 'default', CAST(ROUND(pe.balance * 100.0) AS INTEGER), COALESCE(pe.last_transaction_at, strftime('%s','now'))
FROM player_economy pe;

-- Rebuild economy_transaction with new structure
ALTER TABLE economy_transaction RENAME TO economy_transaction_old;

CREATE TABLE IF NOT EXISTS economy_transaction (
  id               INTEGER PRIMARY KEY AUTOINCREMENT,
  transaction_id   TEXT    NOT NULL CHECK (length(transaction_id) BETWEEN 32 AND 64),
  amount           INTEGER NOT NULL CHECK (amount >= 0),
  type             TEXT    NOT NULL CHECK (type IN ('deposit','withdraw','transfer','reward','purchase','admin_adjust')),
  status           TEXT    NOT NULL CHECK (status IN ('pending','completed','failed','reversed','cancelled')),
  created_at       INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  from_player_id   INTEGER,
  to_player_id     INTEGER,
  note             TEXT,
  metadata         TEXT,
  UNIQUE (transaction_id),
  CHECK (from_player_id IS NOT NULL OR to_player_id IS NOT NULL),
  CHECK (from_player_id IS NULL OR to_player_id IS NULL OR from_player_id <> to_player_id),
  FOREIGN KEY (from_player_id) REFERENCES player(id) ON DELETE SET NULL,
  FOREIGN KEY (to_player_id)   REFERENCES player(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_econ_txn_created_at ON economy_transaction (created_at);
CREATE INDEX IF NOT EXISTS idx_econ_txn_from_player ON economy_transaction (from_player_id, created_at);
CREATE INDEX IF NOT EXISTS idx_econ_txn_to_player   ON economy_transaction (to_player_id, created_at);

-- Migrate old transaction rows into new table
INSERT INTO economy_transaction (
  transaction_id, amount, type, status, created_at,
  from_player_id, to_player_id, note, metadata
)
SELECT
  lower(hex(randomblob(16))) AS transaction_id,
  CAST(ABS(ROUND(et.amount * 100.0)) AS INTEGER) AS amount,
  CASE WHEN et.amount < 0 THEN 'withdraw' ELSE 'deposit' END AS type,
  'completed' AS status,
  et.created_at,
  CASE WHEN et.amount < 0 THEN et.player_id ELSE NULL END AS from_player_id,
  CASE WHEN et.amount >= 0 THEN et.player_id ELSE NULL END AS to_player_id,
  et.reason AS note,
  et.source AS metadata
FROM economy_transaction_old et;

DROP TABLE IF EXISTS economy_transaction_old;

-- NPC expansions: skin and position (owned by NPC)
CREATE TABLE IF NOT EXISTS npc_skin (
  id         INTEGER PRIMARY KEY AUTOINCREMENT,
  npc_id     INTEGER NOT NULL,
  texture    TEXT,
  signature  TEXT,
  is_active  INTEGER NOT NULL DEFAULT 1 CHECK (is_active IN (0,1)),
  created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  updated_at INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  FOREIGN KEY (npc_id) REFERENCES npc(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_npc_skin_npc ON npc_skin (npc_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_npc_skin_one_active ON npc_skin(npc_id) WHERE is_active = 1;

CREATE TABLE IF NOT EXISTS npc_position (
  id         INTEGER PRIMARY KEY AUTOINCREMENT,
  npc_id     INTEGER NOT NULL,
  world      TEXT    NOT NULL,
  x          REAL    NOT NULL,
  y          REAL    NOT NULL,
  z          REAL    NOT NULL,
  yaw        REAL    NOT NULL DEFAULT 0,
  pitch      REAL    NOT NULL DEFAULT 0,
  is_active  INTEGER NOT NULL DEFAULT 1 CHECK (is_active IN (0,1)),
  updated_at INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  FOREIGN KEY (npc_id) REFERENCES npc(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_npc_position_npc ON npc_position (npc_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_npc_position_one_active ON npc_position(npc_id) WHERE is_active = 1;

-- Seed positions from legacy npc table
INSERT OR IGNORE INTO npc_position (npc_id, world, x, y, z, yaw, pitch, is_active, updated_at)
SELECT n.id, n.world, n.x, n.y, n.z, n.yaw, n.pitch, 1, COALESCE(n.updated_at, strftime('%s','now'))
FROM npc n;

-- Menu actions and sessions
CREATE TABLE IF NOT EXISTS menu_action (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  menu_item_id  INTEGER NOT NULL,
  type          TEXT    NOT NULL CHECK (type IN ('command','open_menu','give_item','run_script','custom')),
  payload       TEXT,
  order_index   INTEGER NOT NULL DEFAULT 0 CHECK (order_index >= 0),
  created_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  FOREIGN KEY (menu_item_id) REFERENCES menu_item(id) ON DELETE CASCADE,
  UNIQUE (menu_item_id, order_index)
);
CREATE INDEX IF NOT EXISTS idx_menu_action_item ON menu_action (menu_item_id);

CREATE TABLE IF NOT EXISTS menu_session (
  id         INTEGER PRIMARY KEY AUTOINCREMENT,
  player_id  INTEGER,
  menu_id    INTEGER,
  opened_at  INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  closed_at  INTEGER,
  state      TEXT,
  FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE SET NULL,
  FOREIGN KEY (menu_id)   REFERENCES menu(id)   ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS idx_menu_session_player ON menu_session (player_id, opened_at);
CREATE INDEX IF NOT EXISTS idx_menu_session_open ON menu_session (menu_id) WHERE closed_at IS NULL;

-- Levels and rewards
CREATE TABLE IF NOT EXISTS level (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  level_number  INTEGER NOT NULL CHECK (level_number >= 1),
  name          TEXT    NOT NULL,
  required_xp   INTEGER NOT NULL CHECK (required_xp >= 0),
  description   TEXT,
  created_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  UNIQUE (level_number),
  UNIQUE (name)
);
CREATE INDEX IF NOT EXISTS idx_level_required_xp ON level (required_xp);

CREATE TABLE IF NOT EXISTS level_reward (
  id           INTEGER PRIMARY KEY AUTOINCREMENT,
  level_id     INTEGER NOT NULL,
  reward_type  TEXT    NOT NULL CHECK (reward_type IN ('item','command','permission','currency')),
  payload      TEXT    NOT NULL,
  amount       INTEGER CHECK (amount IS NULL OR amount >= 0),
  order_index  INTEGER NOT NULL DEFAULT 0 CHECK (order_index >= 0),
  FOREIGN KEY (level_id) REFERENCES level(id) ON DELETE CASCADE,
  UNIQUE (level_id, order_index)
);
CREATE INDEX IF NOT EXISTS idx_level_reward_level ON level_reward (level_id);

-- Extend existing player_level to link to level table (non-destructive)
ALTER TABLE player_level ADD COLUMN current_level_id INTEGER;
ALTER TABLE player_level ADD COLUMN updated_at INTEGER NOT NULL DEFAULT (strftime('%s','now'));
CREATE INDEX IF NOT EXISTS idx_player_level_level ON player_level (current_level_id);

-- Migration version snapshot (in addition to legacy schema_migration)
CREATE TABLE IF NOT EXISTS migration_version (
  id         INTEGER PRIMARY KEY CHECK (id = 1),
  version    INTEGER NOT NULL,
  applied_at INTEGER NOT NULL DEFAULT (strftime('%s','now'))
);
INSERT INTO migration_version (id, version, applied_at)
VALUES (1, 2, strftime('%s','now'))
ON CONFLICT(id) DO UPDATE SET version=excluded.version, applied_at=excluded.applied_at;

-- Record this migration in legacy schema_migration
INSERT INTO schema_migration(version, description, applied_at)
SELECT 2, 'Plugin features schema', strftime('%s','now')
WHERE NOT EXISTS (SELECT 1 FROM schema_migration WHERE version = 2);

COMMIT;
