-- V3__npc_behavior_and_logs: Add npc_behavior (1-1) and npc_interaction_log (1-N)
-- Idempotent and additive.

BEGIN;

-- NPC behavior (one row per NPC)
CREATE TABLE IF NOT EXISTS npc_behavior (
  id                 INTEGER PRIMARY KEY AUTOINCREMENT,
  npc_id             INTEGER NOT NULL UNIQUE,
  behavior_json      TEXT    NOT NULL DEFAULT '{}',
  click_cooldown_ms  INTEGER NOT NULL DEFAULT 300,
  updated_at         INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  FOREIGN KEY (npc_id) REFERENCES npc(id) ON DELETE CASCADE
);

-- Interaction logs
CREATE TABLE IF NOT EXISTS npc_interaction_log (
  id           INTEGER PRIMARY KEY AUTOINCREMENT,
  npc_id       INTEGER NOT NULL,
  player_id    INTEGER,
  action       TEXT    NOT NULL,
  meta_json    TEXT,
  created_at   INTEGER NOT NULL DEFAULT (strftime('%s','now')),
  FOREIGN KEY (npc_id)    REFERENCES npc(id)    ON DELETE CASCADE,
  FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_npc_log_npc_time    ON npc_interaction_log (npc_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_npc_log_player_time ON npc_interaction_log (player_id, created_at DESC);

-- Update migration tracking tables
INSERT INTO migration_version (id, version, applied_at)
VALUES (1, 3, strftime('%s','now'))
ON CONFLICT(id) DO UPDATE SET version=excluded.version, applied_at=excluded.applied_at;

INSERT INTO schema_migration(version, description, applied_at)
SELECT 3, 'NPC behavior + interaction logs', strftime('%s','now')
WHERE NOT EXISTS (SELECT 1 FROM schema_migration WHERE version = 3);

COMMIT;
