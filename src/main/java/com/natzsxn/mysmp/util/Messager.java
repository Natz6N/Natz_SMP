// FILE: src/main/java/com/natzsxn/mysmp/util/Messager.java
package com.natzsxn.mysmp.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;

public class Messager {
    private final String prefix;

    public Messager(String prefix) {
        this.prefix = prefix;
    }

    public void send(CommandSender sender, String message) {
        sender.sendMessage(format(message));
    }

    public String success(String msg) { return format(msg); }
    public String error(String msg) { return format("&c" + msg); }
    public String warn(String msg) { return format("&e" + msg); }
    public void info(String msg) { org.bukkit.Bukkit.getLogger().info("[MySMP] " + msg); }

    private String format(String message) {
        Component c = LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + message);
        return LegacyComponentSerializer.legacySection().serialize(c);
    }
}
