-- Optional seed data for NatzSMP.
-- This file is not executed automatically; it can be used for manual seeding
-- or future tooling.

BEGIN;

-- Config defaults
INSERT OR IGNORE INTO config(key, value, updated_at) VALUES
  ('economy_default_balance', '0', strftime('%s','now')),
  ('level_max', '100', strftime('%s','now'));

-- Players
INSERT OR IGNORE INTO player(uuid, name, first_seen_at, last_seen_at, last_ip)
VALUES
 ('00000000-0000-0000-0000-000000000001','Alpha',  strftime('%s','now'), strftime('%s','now'), '127.0.0.1'),
 ('00000000-0000-0000-0000-000000000002','Bravo',  strftime('%s','now'), strftime('%s','now'), '127.0.0.1'),
 ('00000000-0000-0000-0000-000000000003','Charlie',strftime('%s','now'), strftime('%s','now'), '127.0.0.1'),
 ('00000000-0000-0000-0000-000000000004','Delta',  strftime('%s','now'), strftime('%s','now'), '127.0.0.1'),
 ('00000000-0000-0000-0000-000000000005','Echo',   strftime('%s','now'), strftime('%s','now'), '127.0.0.1');

-- Economy balances (in cents)
INSERT OR IGNORE INTO economy_balance(player_id, currency, amount, updated_at)
SELECT p.id, 'default', v.amount, strftime('%s','now')
FROM (
  SELECT '00000000-0000-0000-0000-000000000001' AS u, 500000 AS amount UNION ALL
  SELECT '00000000-0000-0000-0000-000000000002', 250000 UNION ALL
  SELECT '00000000-0000-0000-0000-000000000003', 100000 UNION ALL
  SELECT '00000000-0000-0000-0000-000000000004',  50000 UNION ALL
  SELECT '00000000-0000-0000-0000-000000000005',      0
) v
JOIN player p ON p.uuid = v.u;

-- Levels 1..10
INSERT OR IGNORE INTO level(level_number, name, required_xp, description, created_at) VALUES
 (1,'Beginner',0,'',strftime('%s','now')),
 (2,'Apprentice',100,'',strftime('%s','now')),
 (3,'Adept',300,'',strftime('%s','now')),
 (4,'Journeyman',600,'',strftime('%s','now')),
 (5,'Expert',1000,'',strftime('%s','now')),
 (6,'Elite',1500,'',strftime('%s','now')),
 (7,'Master',2100,'',strftime('%s','now')),
 (8,'Grandmaster',2800,'',strftime('%s','now')),
 (9,'Legend',3600,'',strftime('%s','now')),
 (10,'Mythic',4500,'',strftime('%s','now'));

-- Rewards
INSERT OR IGNORE INTO level_reward(level_id, reward_type, payload, amount, order_index)
SELECT l.id, 'currency', 'main', 5000, 0 FROM level l WHERE l.level_number=2;
INSERT OR IGNORE INTO level_reward(level_id, reward_type, payload, amount, order_index)
SELECT l.id, 'currency', 'main', 10000, 0 FROM level l WHERE l.level_number=3;
INSERT OR IGNORE INTO level_reward(level_id, reward_type, payload, amount, order_index)
SELECT l.id, 'command', 'give {player} minecraft:diamond 1', NULL, 0 FROM level l WHERE l.level_number=5;

-- Player leveling
INSERT OR IGNORE INTO player_level(player_id, level, xp, total_xp_earned, last_level_up_at)
SELECT p.id, 2, 120, 120, strftime('%s','now') FROM player p WHERE p.uuid='00000000-0000-0000-0000-000000000001';
INSERT OR IGNORE INTO player_level(player_id, level, xp, total_xp_earned)
SELECT p.id, 1, 50, 50 FROM player p WHERE p.uuid='00000000-0000-0000-0000-000000000002';

-- Menu and items (requires V4 for price_cents)
INSERT OR IGNORE INTO menu(name, title, rows, created_at, updated_at)
VALUES ('shop_main','Shop',3,strftime('%s','now'),strftime('%s','now'));

INSERT OR IGNORE INTO menu_item(menu_id, slot, material, amount, display_name, action_type, action_payload, enabled, price_cents)
SELECT m.id, 11, 'IRON_SWORD', 1, 'Iron Sword', 'give_item', '{"material":"IRON_SWORD","amount":1}', 1, 5000 FROM menu m WHERE m.name='shop_main';
INSERT OR IGNORE INTO menu_item(menu_id, slot, material, amount, display_name, action_type, action_payload, enabled, price_cents)
SELECT m.id, 13, 'DIAMOND', 1, 'Diamond', 'give_item', '{"material":"DIAMOND","amount":1}', 1, 20000 FROM menu m WHERE m.name='shop_main';
INSERT OR IGNORE INTO menu_item(menu_id, slot, material, amount, display_name, action_type, action_payload, enabled, price_cents)
SELECT m.id, 15, 'BREAD', 16, 'Bread x16', 'give_item', '{"material":"BREAD","amount":16}', 1, 2500 FROM menu m WHERE m.name='shop_main';

-- NPCs
INSERT OR IGNORE INTO npc(name, world, x, y, z, yaw, pitch, enabled, created_at, updated_at)
VALUES
 ('Elder Oak','world',100.5,64.0,200.5,90,0,1,strftime('%s','now'),strftime('%s','now')),
 ('Vault Keeper','world',105.5,64.0,200.5,90,0,1,strftime('%s','now'),strftime('%s','now')),
 ('Trader Tim','world',110.5,64.0,200.5,90,0,1,strftime('%s','now'),strftime('%s','now'));

-- Example economy transaction (deposit)
INSERT INTO economy_transaction(transaction_id, amount, type, status, created_at, to_player_id, note)
SELECT lower(hex(randomblob(16))), 10000, 'deposit', 'completed', strftime('%s','now'), p.id, 'Initial bonus'
FROM player p WHERE p.uuid='00000000-0000-0000-0000-000000000005';

COMMIT;
