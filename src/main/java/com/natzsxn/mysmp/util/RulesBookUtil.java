package com.natzsxn.mysmp.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class RulesBookUtil {
    public static final String KEY = "rules_book";

    public static final String METADATA_PENDING = "mysmp_rules_book_pending";

    public static void giveBookIfNeeded(JavaPlugin plugin, Player p, Messager messager) {
        // Cek cepat: apakah pemain sudah punya rules book
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && isRulesBook(plugin, item)) {
                return;
            }
        }

        // Proteksi race: tandai pending supaya tidak menjalankan 2x pada tick yg sama
        if (p.hasMetadata(METADATA_PENDING))
            return;
        p.setMetadata(METADATA_PENDING, new FixedMetadataValue(plugin, true));

        Bukkit.getScheduler().runTask(plugin, () -> {
            // re-check di main thread
            for (ItemStack item : p.getInventory().getContents()) {
                if (item != null && isRulesBook(plugin, item)) {
                    p.removeMetadata(METADATA_PENDING, plugin);
                    return;
                }
            }

            ItemStack book = create(plugin, p);
            int empty = p.getInventory().firstEmpty();
            if (empty == -1) {
                messager.warn("Inventory full. Book will be given later.");
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    int slot = p.getInventory().firstEmpty();
                    if (slot != -1)
                        p.getInventory().setItem(slot, book);
                }, 40L);
            } else {
                if (p.getInventory().getItem(0) == null) {
                    p.getInventory().setItem(0, book);
                } else {
                    p.getInventory().setItem(empty, book);
                }
            }

            p.removeMetadata(METADATA_PENDING, plugin);
        });
    }

    public static ItemStack create(JavaPlugin plugin, Player player) {
        ItemStack book = new ItemStack(MaterialCompat.writtenBook(), 1);
        BookMeta meta = (BookMeta) book.getItemMeta();

        if (meta != null) {
            meta.setTitle("Server Guide");
            meta.setAuthor("Server");

            String page1 = "Welcome " + player.getName() + " di serverku!\n\n" +
                    "Perkenalkan saya Natz, selaku owner dan founder dari server SMP.\n" +
                    "Jika ada keluhan, bilang saja ke media sosial saya.\n\n" +
                    "Tujuan saya membuat server YTTA karena saya gabut, jadi ya buat aja.\n" +
                    "Untuk fitur selanjutnya — idk.\n\n" +
                    "Whatsapp:\n" +
                    "Instagram:\n";

            String page2 = "Rule Server:\n\n" +
                    "1) Dilarang grief\n" +
                    "2) Dilarang toxic\n" +
                    "3) Hormati sesama pemain\n" +
                    "4) Ikuti arahan staff\n\n" +
                    "Gunakan /sayasetuju untuk setuju.";

            // Tambahkan halaman
            meta.addPage(page1, page2);

            // Persistent data (optional, tetap kamu pakai)
            NamespacedKey key = new NamespacedKey(plugin, KEY);
            meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

            book.setItemMeta(meta);
        }

        return book;
    }

    public static boolean isRulesBook(JavaPlugin plugin, ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return false;
        if (!(item.getItemMeta() instanceof BookMeta))
            return false;
        NamespacedKey key = new NamespacedKey(plugin, KEY);
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }
}
