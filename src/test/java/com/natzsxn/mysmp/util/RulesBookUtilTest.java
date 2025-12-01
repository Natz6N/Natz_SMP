package com.natzsxn.mysmp.util;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.natzsxn.mysmp.Main;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RulesBookUtilTest {
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
    public void testGiveBookOnlyOnce() {
        PlayerMock p = server.addPlayer();
        Messager msg = new Messager(plugin, "&a[MySMP] ");

        RulesBookUtil.giveBookIfNeeded(plugin, p, msg);
        server.getScheduler().performTicks(1L);

        // Attempt to give again quickly (simulates duplicate call from join/respawn)
        RulesBookUtil.giveBookIfNeeded(plugin, p, msg);
        server.getScheduler().performTicks(1L);

        int count = 0;
        for (ItemStack it : p.getInventory().getContents()) {
            if (it != null && RulesBookUtil.isRulesBook(plugin, it)) count++;
        }
        Assertions.assertEquals(1, count);
    }
}
