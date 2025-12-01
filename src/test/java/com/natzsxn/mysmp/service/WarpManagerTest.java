package com.natzsxn.mysmp.service;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.natzsxn.mysmp.Main;
import com.natzsxn.mysmp.config.ConfigManager;
import com.natzsxn.mysmp.storage.DatabaseManager;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class WarpManagerTest {
    private static ServerMock server;
    private static Main plugin;

    @BeforeAll
    public static void setup() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Main.class);
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testPreLobbySaveAndRestore() {
        WorldMock survival = new WorldMock("survival");
        WorldMock lobby = new WorldMock("lobby");
        server.addWorld(survival);
        server.addWorld(lobby);

        ConfigManager cfg = new ConfigManager(plugin);
        cfg.loadAll();
        cfg.setSpawn("lobby", new Location(lobby, 5.5, 84.0, 18.22, 45f, 10f));
        cfg.setSpawn("survival", new Location(survival, 0, 80, 0, 0f, 0f));

        Messager msg = new Messager(plugin, cfg.getPrefix());

        DatabaseManager db = new DatabaseManager(plugin, msg, "test.sqlite");
        db.init();

        WarpManager wm = new WarpManager(plugin, cfg, db, msg);

        PlayerMock player = server.addPlayer();
        player.setOp(true);
        player.teleport(new Location(survival, 100.25, 64.75, -23.5, 90f, 12f));

        wm.warp(player, "lobby");
        server.getScheduler().performTicks(20L);

        wm.warp(player, "survival");
        server.getScheduler().performTicks(20L);

        Location restored = player.getLocation();
        Assertions.assertEquals("survival", restored.getWorld().getName());
        Assertions.assertEquals(100.25, restored.getX());
        Assertions.assertEquals(64.75, restored.getY());
        Assertions.assertEquals(-23.5, restored.getZ());
        Assertions.assertEquals(90f, restored.getYaw());
        Assertions.assertEquals(12f, restored.getPitch());
    }

    @Test
    public void testDoubleTeleportProtection() {
        WorldMock lobby = new WorldMock("lobby");
        server.addWorld(lobby);

        ConfigManager cfg = new ConfigManager(plugin);
        cfg.loadAll();
        cfg.setSpawn("lobby", new Location(lobby, 5.5, 84.0, 18.22, 45f, 10f));

        Messager msg = new Messager(plugin, cfg.getPrefix());
        DatabaseManager db = new DatabaseManager(plugin, msg, "test2.sqlite");
        db.init();
        WarpManager wm = new WarpManager(plugin, cfg, db, msg);

        PlayerMock p = server.addPlayer();

        wm.warp(p, "lobby");
        // Call warp again immediately to trigger protection
        wm.warp(p, "lobby");
        server.getScheduler().performTicks(20L);

        String last = p.nextMessage();
        boolean sawPending = false;
        while (last != null) {
            if (last.contains("Teleport is pending")) { sawPending = true; break; }
            last = p.nextMessage();
        }
        Assertions.assertTrue(sawPending);
    }
}
