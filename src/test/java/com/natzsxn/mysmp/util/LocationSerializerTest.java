// FILE: src/test/java/com/natzsxn/mysmp/util/LocationSerializerTest.java
package com.natzsxn.mysmp.util;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.WorldMock;
import org.bukkit.Location;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LocationSerializerTest {
    private static WorldMock world;

    @BeforeAll
    public static void setup() {
        MockBukkit.mock();
        world = new WorldMock("testworld");
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testSerializeDeserialize() {
        Location loc = new Location(world, 10.5, 64, -3.25, 90f, 0f);
        String s = LocationSerializer.serialize(loc);
        Location loc2 = LocationSerializer.deserialize(s);
        Assertions.assertEquals(loc.getWorld().getName(), loc2.getWorld().getName());
        Assertions.assertEquals(loc.getX(), loc2.getX());
        Assertions.assertEquals(loc.getY(), loc2.getY());
        Assertions.assertEquals(loc.getZ(), loc2.getZ());
        Assertions.assertEquals(loc.getYaw(), loc2.getYaw());
        Assertions.assertEquals(loc.getPitch(), loc2.getPitch());
    }
}
