-- ============================================================
-- SQLite schema for plugin features (documentation/seed)
-- Notes:
-- - INTEGER PRIMARY KEY AUTOINCREMENT is used for all table PKs for locality, performance, and simplicity.
--   UUIDs are used only where an externally visible/stable identifier is required (e.g., economy_transaction.transaction_id).
-- - All timestamps are Unix epoch seconds stored as INTEGER.
-- - Enable FK enforcement and WAL for better concurrency.
-- ============================================================

PRAGMA foreign_keys = ON;              -- Enforce foreign key constraints
PRAGMA journal_mode = WAL;             -- WAL improves concurrency for read-heavy workloads

-- ============================================================
-- Table: player
-- Purpose: Canonical player list.
-- Rationale:
-- - Keep auto-increment integer PK for compact indexes.
-- - Expose a stable UUID TEXT for external references; unique but not the PK.
-- - ON DELETE effects are defined in child tables to either CASCADE (owned data) or SET NULL (audit trails).
-- ============================================================
CREATE TABLE IF NOT EXISTS player (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  uuid          TEXT    NOT NULL,                                   -- Externally visible stable ID (RFC 4122 string)
  name          TEXT    NOT NULL,
  status        TEXT    NOT NULL DEFAULT 'active' CHECK (status IN ('active','inactive','banned')),
  created_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  updated_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  last_login    INTEGER,
  UNIQUE (uuid),
  UNIQUE (name)
);

CREATE INDEX IF NOT EXISTS idx_player_created_at ON player (created_at);
CREATE INDEX IF NOT EXISTS idx_player_last_login ON player (last_login);

-- ============================================================
-- Table: player_profile
-- Purpose: Optional/extended profile for a player.
-- FK: ON DELETE CASCADE because profile is owned by player.
-- ============================================================
CREATE TABLE IF NOT EXISTS player_profile (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  player_id     INTEGER NOT NULL,
  display_name  TEXT,
  country       TEXT,
  language      TEXT,
  metadata      TEXT,                                -- Optional JSON/text blob
  created_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  updated_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  UNIQUE (player_id),
  FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_player_profile_player ON player_profile (player_id);

-- ============================================================
-- Table: economy_balance
-- Purpose: Current balances per player, optionally per currency.
-- FK: ON DELETE CASCADE to remove balances for deleted players (history remains in transactions).
-- ============================================================
CREATE TABLE IF NOT EXISTS economy_balance (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  player_id     INTEGER NOT NULL,
  currency      TEXT    NOT NULL DEFAULT 'default',
  amount        INTEGER NOT NULL DEFAULT 0 CHECK (amount >= 0),    -- Store smallest unit (integer)
  updated_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  UNIQUE (player_id, currency),
  FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_econ_balance_player ON economy_balance (player_id);
CREATE INDEX IF NOT EXISTS idx_econ_balance_currency ON economy_balance (currency);

-- ============================================================
-- Table: economy_transaction
-- Purpose: Immutable transaction log for the economy.
-- UUID: transaction_id is TEXT because it must be stable/external; enforced unique.
-- FKs: ON DELETE SET NULL to preserve audit integrity even if a player is deleted.
-- Checks:
-- - amount >= 0 (absolute value; direction implied by type/from/to).
-- - at least one of from_player_id or to_player_id must be non-NULL.
-- - from != to when both present.
-- ============================================================
CREATE TABLE IF NOT EXISTS economy_transaction (
  id               INTEGER PRIMARY KEY AUTOINCREMENT,
  transaction_id   TEXT    NOT NULL CHECK (length(transaction_id) BETWEEN 32 AND 64),
  amount           INTEGER NOT NULL CHECK (amount >= 0),
  type             TEXT    NOT NULL CHECK (type IN ('deposit','withdraw','transfer','reward','purchase','admin_adjust')),
  status           TEXT    NOT NULL CHECK (status IN ('pending','completed','failed','reversed','cancelled')),
  created_at       INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  from_player_id   INTEGER,                         -- NULL if system/source-less credit
  to_player_id     INTEGER,                         -- NULL if system/sink debit
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

-- ============================================================
-- Table: npc
-- Purpose: Non-player characters registry.
-- Child tables (skin/position) CASCADE on delete because they are owned by the NPC.
-- ============================================================
CREATE TABLE IF NOT EXISTS npc (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  name          TEXT    NOT NULL,
  type          TEXT,
  is_active     INTEGER NOT NULL DEFAULT 1 CHECK (is_active IN (0,1)),
  created_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  updated_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  UNIQUE (name)
);

CREATE INDEX IF NOT EXISTS idx_npc_active ON npc (is_active);

-- ============================================================
-- Table: npc_skin
-- Purpose: Skins for NPCs; allow multiple historical skins but only one active at a time.
-- FK: ON DELETE CASCADE (owned by NPC).
-- Partial unique index ensures at most one active skin per NPC.
-- ============================================================
CREATE TABLE IF NOT EXISTS npc_skin (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  npc_id        INTEGER NOT NULL,
  texture       TEXT,          -- E.g., base64 or URL
  signature     TEXT,          -- E.g., Mojang skin signature
  is_active     INTEGER NOT NULL DEFAULT 1 CHECK (is_active IN (0,1)),
  created_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  updated_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  FOREIGN KEY (npc_id) REFERENCES npc(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_npc_skin_npc ON npc_skin (npc_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_npc_skin_one_active
  ON npc_skin(npc_id) WHERE is_active = 1;

-- ============================================================
-- Table: npc_position
-- Purpose: NPC positions; allow history but only one active position per NPC.
-- FK: ON DELETE CASCADE (owned by NPC).
-- ============================================================
CREATE TABLE IF NOT EXISTS npc_position (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  npc_id        INTEGER NOT NULL,
  world         TEXT    NOT NULL,
  x             REAL    NOT NULL,
  y             REAL    NOT NULL,
  z             REAL    NOT NULL,
  yaw           REAL    NOT NULL DEFAULT 0,
  pitch         REAL    NOT NULL DEFAULT 0,
  is_active     INTEGER NOT NULL DEFAULT 1 CHECK (is_active IN (0,1)),
  updated_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  FOREIGN KEY (npc_id) REFERENCES npc(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_npc_position_npc ON npc_position (npc_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_npc_position_one_active
  ON npc_position(npc_id) WHERE is_active = 1;

-- ============================================================
-- Table: menu
-- Purpose: Menu definitions.
-- ============================================================
CREATE TABLE IF NOT EXISTS menu (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  key           TEXT    NOT NULL,                             -- Stable identifier for code/config
  title         TEXT    NOT NULL,
  description   TEXT,
  is_active     INTEGER NOT NULL DEFAULT 1 CHECK (is_active IN (0,1)),
  created_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  updated_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  UNIQUE (key)
);

CREATE INDEX IF NOT EXISTS idx_menu_active ON menu (is_active);

-- ============================================================
-- Table: menu_item
-- Purpose: Items within a menu (e.g., GUI slots).
-- FK: ON DELETE CASCADE because items belong to the menu.
-- ============================================================
CREATE TABLE IF NOT EXISTS menu_item (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  menu_id       INTEGER NOT NULL,
  slot          INTEGER NOT NULL CHECK (slot >= 0),
  label         TEXT    NOT NULL,
  icon          TEXT,
  permission    TEXT,                                        -- Permission required to click/see
  enabled       INTEGER NOT NULL DEFAULT 1 CHECK (enabled IN (0,1)),
  created_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  updated_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  UNIQUE (menu_id, slot),
  FOREIGN KEY (menu_id) REFERENCES menu(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_menu_item_menu ON menu_item (menu_id);

-- ============================================================
-- Table: menu_action
-- Purpose: Actions performed when a menu item is activated; multiple actions per item allowed.
-- FK: ON DELETE CASCADE (owned by item).
-- ============================================================
CREATE TABLE IF NOT EXISTS menu_action (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  menu_item_id  INTEGER NOT NULL,
  type          TEXT    NOT NULL CHECK (type IN ('command','open_menu','give_item','run_script','custom')),
  payload       TEXT,                                        -- JSON/config for action
  order_index   INTEGER NOT NULL DEFAULT 0 CHECK (order_index >= 0),
  created_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  FOREIGN KEY (menu_item_id) REFERENCES menu_item(id) ON DELETE CASCADE,
  UNIQUE (menu_item_id, order_index)
);

CREATE INDEX IF NOT EXISTS idx_menu_action_item ON menu_action (menu_item_id);

-- ============================================================
-- Table: menu_session
-- Purpose: Track open/closed menu sessions by player.
-- FKs: SET NULL to keep audit record if player/menu definitions are removed.
-- ============================================================
CREATE TABLE IF NOT EXISTS menu_session (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  player_id     INTEGER,
  menu_id       INTEGER,
  opened_at     INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  closed_at     INTEGER,                                     -- NULL means still open
  state         TEXT,                                        -- JSON/state blob
  FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE SET NULL,
  FOREIGN KEY (menu_id)   REFERENCES menu(id)   ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_menu_session_player ON menu_session (player_id, opened_at);
CREATE INDEX IF NOT EXISTS idx_menu_session_open ON menu_session (menu_id) WHERE closed_at IS NULL;

-- ============================================================
-- Table: level
-- Purpose: Level definitions with XP thresholds.
-- ============================================================
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

-- ============================================================
-- Table: player_level
-- Purpose: Per-player level progression.
-- FKs:
-- - player: CASCADE (record is owned by player).
-- - current_level_id: SET NULL to retain row if a level definition is removed.
-- ============================================================
CREATE TABLE IF NOT EXISTS player_level (
  id                INTEGER PRIMARY KEY AUTOINCREMENT,
  player_id         INTEGER NOT NULL,
  current_level_id  INTEGER,
  xp                INTEGER NOT NULL DEFAULT 0 CHECK (xp >= 0),
  updated_at        INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  UNIQUE (player_id),
  FOREIGN KEY (player_id)        REFERENCES player(id) ON DELETE CASCADE,
  FOREIGN KEY (current_level_id) REFERENCES level(id)  ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_player_level_player ON player_level (player_id);
CREATE INDEX IF NOT EXISTS idx_player_level_level  ON player_level (current_level_id);

-- ============================================================
-- Table: level_reward
-- Purpose: Rewards granted upon reaching a level.
-- FK: ON DELETE CASCADE because rewards are owned by level definitions.
-- ============================================================
CREATE TABLE IF NOT EXISTS level_reward (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  level_id      INTEGER NOT NULL,
  reward_type   TEXT    NOT NULL CHECK (reward_type IN ('item','command','permission','currency')),
  payload       TEXT    NOT NULL,                             -- Parameters/JSON for reward
  amount        INTEGER CHECK (amount IS NULL OR amount >= 0),
  order_index   INTEGER NOT NULL DEFAULT 0 CHECK (order_index >= 0),
  FOREIGN KEY (level_id) REFERENCES level(id) ON DELETE CASCADE,
  UNIQUE (level_id, order_index)
);

CREATE INDEX IF NOT EXISTS idx_level_reward_level ON level_reward (level_id);

-- ============================================================
-- Table: command_log
-- Purpose: Audit trail of commands executed by players (or system).
-- FK: SET NULL to preserve audit record if player is removed.
-- ============================================================
CREATE TABLE IF NOT EXISTS command_log (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  player_id     INTEGER,
  command       TEXT    NOT NULL,
  args          TEXT,
  executed_at   INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  success       INTEGER NOT NULL DEFAULT 1 CHECK (success IN (0,1)),
  output        TEXT,
  FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_command_log_time       ON command_log (executed_at);
CREATE INDEX IF NOT EXISTS idx_command_log_playertime ON command_log (player_id, executed_at);

-- ============================================================
-- Table: config
-- Purpose: Key-value configuration store with optional description/secret flag.
-- ============================================================
CREATE TABLE IF NOT EXISTS config (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  key           TEXT    NOT NULL,
  value         TEXT    NOT NULL,
  description   TEXT,
  is_secret     INTEGER NOT NULL DEFAULT 0 CHECK (is_secret IN (0,1)),
  updated_at    INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  UNIQUE (key)
);

CREATE INDEX IF NOT EXISTS idx_config_updated_at ON config (updated_at);

-- ============================================================
-- Table: migration_version
-- Purpose: Tracks schema/migration version. Singleton row enforced by CHECK(id = 1).
-- Usage: Insert/Update this single row during migrations.
-- ============================================================
CREATE TABLE IF NOT EXISTS migration_version (
  id         INTEGER PRIMARY KEY CHECK (id = 1), -- Always 1; ensures exactly one row
  version    INTEGER NOT NULL,
  applied_at INTEGER NOT NULL DEFAULT (strftime('%s','now'))
);

-- Optional helper to ensure quick lookups by version if multiple rows are ever allowed in future.
CREATE UNIQUE INDEX IF NOT EXISTS idx_migration_singleton ON migration_version (id);

-- ============================================================
-- End of schema
-- ============================================================
