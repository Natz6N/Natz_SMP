package com.natzsxn.mysmp.util;

import org.bukkit.Material;

/**
 * MaterialCompat: util aman untuk konversi string ke Material modern.
 * Menghindari inisialisasi CraftLegacy dengan tidak menggunakan angka/nilai legacy.
 */
public class MaterialCompat {
    public static Material of(String name, Material fallback) {
        try {
            return Material.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return fallback;
        }
    }

    public static Material writtenBook() {
        return Material.WRITTEN_BOOK;
    }
}
