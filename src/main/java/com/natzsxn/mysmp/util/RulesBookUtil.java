// FILE: src/main/java/com/natzsxn/mysmp/util/RulesBookUtil.java
package com.natzsxn.mysmp.util;

import com.natzsxn.mysmp.config.Messages;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class RulesBookUtil {
    public static final String KEY = "rules_book";

    public static ItemStack create(JavaPlugin plugin, Messages messages) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle("Server Rules");
        meta.setAuthor("Server");
        meta.addPage("Welcome!\n\n1) Be respectful\n2) No griefing\n3) Follow staff guidance\n\nUse /sayasetuju to agree.");
        NamespacedKey key = new NamespacedKey(plugin, KEY);
        meta.getPersistentDataContainer().set(key, org.bukkit.persistence.PersistentDataType.BYTE, (byte)1);
        book.setItemMeta(meta);
        return book;
    }

    public static boolean isRulesBook(JavaPlugin plugin, ItemStack item) {
        if (item == null) return false;
        if (!(item.getItemMeta() instanceof BookMeta)) return false;
        NamespacedKey key = new NamespacedKey(plugin, KEY);
        return item.getItemMeta().getPersistentDataContainer().has(key, org.bukkit.persistence.PersistentDataType.BYTE);
    }
}
