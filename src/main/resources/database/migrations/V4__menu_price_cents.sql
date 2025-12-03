-- V4__menu_price_cents: Add price_cents to menu_item and enforce uniqueness on (menu_id, slot)
BEGIN;
ALTER TABLE menu_item ADD COLUMN price_cents INTEGER NOT NULL DEFAULT 0 CHECK (price_cents >= 0);
CREATE UNIQUE INDEX IF NOT EXISTS uq_menu_item_menu_slot ON menu_item(menu_id, slot);
INSERT INTO migration_version (id, version, applied_at)
VALUES (1, 4, strftime('%s','now'))
ON CONFLICT(id) DO UPDATE SET version=excluded.version, applied_at=excluded.applied_at;
INSERT INTO schema_migration(version, description, applied_at)
SELECT 4, 'Menu item price_cents and uniqueness', strftime('%s','now')
WHERE NOT EXISTS (SELECT 1 FROM schema_migration WHERE version = 4);
COMMIT;
