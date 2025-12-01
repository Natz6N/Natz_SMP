package com.natzsxn.mysmp.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.natzsxn.mysmp.Main;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class OwnerCommandTest {
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
    public void testOwnerCommandAccess() {
        PlayerMock owner = server.addPlayer();
        owner.setOp(true);

        plugin.getConfig().set("settings.owner-uuid", owner.getUniqueId().toString());
        plugin.saveConfig();

        boolean result = server.dispatchCommand(owner, "owner reload");
        Assertions.assertTrue(result);
        String msg = owner.nextMessage();
        Assertions.assertTrue(msg.contains("Configuration reloaded"));
    }
}
