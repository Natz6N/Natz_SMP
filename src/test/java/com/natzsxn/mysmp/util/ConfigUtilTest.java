// FILE: src/test/java/com/natzsxn/mysmp/util/ConfigUtilTest.java
package com.natzsxn.mysmp.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigUtilTest {
    @Test
    public void testGetters() {
        YamlConfiguration y = new YamlConfiguration();
        y.set("a", "b");
        y.set("i", 5);
        y.set("b", true);
        Assertions.assertEquals("b", ConfigUtil.getString(y, "a", "x"));
        Assertions.assertEquals(5, ConfigUtil.getInt(y, "i", 0));
        Assertions.assertTrue(ConfigUtil.getBoolean(y, "b", false));
        Assertions.assertEquals("x", ConfigUtil.getString(y, "missing", "x"));
    }
}
