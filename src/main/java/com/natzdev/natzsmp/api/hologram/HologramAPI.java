package com.natzdev.natzsmp.api.hologram;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Utility class untuk operasi hologram yang umum.
 * Menyediakan static helper methods untuk kemudahan penggunaan.
 * Mirip dengan DHAPI dari DecentHolograms.
 */
public final class HologramAPI {
    private static HologramService service;

    private HologramAPI() {
        // Utility class, tidak boleh di-instantiate
    }

    /**
     * Mengatur HologramService yang akan digunakan
     */
    public static void setService(HologramService hologramService) {
        service = hologramService;
    }

    /**
     * Mendapatkan HologramService yang sedang digunakan
     */
    public static HologramService getService() {
        if (service == null) {
            throw new IllegalStateException("HologramService belum diatur. Panggil setService() terlebih dahulu.");
        }
        return service;
    }

    // ==================== CREATE HOLOGRAM ====================

    /**
     * Membuat hologram baru dengan nama dan lokasi
     */
    public static Hologram createHologram(String name, Location location) {
        return getService().create(name, location);
    }

    /**
     * Membuat hologram baru dengan nama, lokasi, dan lines
     */
    public static Hologram createHologram(String name, Location location, List<Component> lines) {
        return getService().create(name, location, lines);
    }

    /**
     * Membuat hologram baru dengan nama, lokasi, dan save to file option
     */
    public static Hologram createHologram(String name, Location location, boolean saveToFile) {
        return getService().create(name, location, saveToFile);
    }

    /**
     * Membuat hologram baru dengan nama, lokasi, lines, dan save to file option
     */
    public static Hologram createHologram(String name, Location location, List<Component> lines, boolean saveToFile) {
        return getService().create(name, location, lines, saveToFile);
    }

    /**
     * Membuat hologram baru dengan nama, lokasi, dan single line text
     */
    public static Hologram createHologram(String name, Location location, Component line) {
        List<Component> lines = new ArrayList<>();
        lines.add(line);
        return getService().create(name, location, lines);
    }

    // ==================== GET HOLOGRAM ====================

    /**
     * Mendapatkan hologram berdasarkan UUID
     */
    public static Optional<Hologram> getHologram(UUID id) {
        return getService().get(id);
    }

    /**
     * Mendapatkan hologram berdasarkan nama
     */
    public static Optional<Hologram> getHologram(String name) {
        return getService().getByName(name);
    }

    /**
     * Mendapatkan semua hologram
     */
    public static java.util.Collection<Hologram> getAllHolograms() {
        return getService().getAll();
    }

    // ==================== DELETE HOLOGRAM ====================

    /**
     * Menghapus hologram berdasarkan UUID
     */
    public static void deleteHologram(UUID id) {
        getService().remove(id);
    }

    /**
     * Menghapus hologram berdasarkan nama
     */
    public static void deleteHologram(String name) {
        getService().removeByName(name);
    }

    /**
     * Menghapus semua hologram
     */
    public static void deleteAllHolograms() {
        getService().removeAll();
    }

    // ==================== MOVE HOLOGRAM ====================

    /**
     * Memindahkan hologram ke lokasi baru
     */
    public static void moveHologram(Hologram hologram, Location location) {
        if (hologram == null) {
            throw new IllegalArgumentException("Hologram tidak boleh null");
        }
        if (location == null) {
            throw new IllegalArgumentException("Location tidak boleh null");
        }
        hologram.setLocation(location);
        hologram.realignLines();
        hologram.save();
    }

    /**
     * Memindahkan hologram berdasarkan nama
     */
    public static void moveHologram(String name, Location location) {
        Optional<Hologram> hologram = getService().getByName(name);
        if (hologram.isPresent()) {
            moveHologram(hologram.get(), location);
        }
    }

    // ==================== SHOW/HIDE HOLOGRAM ====================

    /**
     * Menampilkan hologram ke pemain
     */
    public static void showHologram(Hologram hologram, Player player) {
        if (hologram != null && player != null) {
            hologram.show(player);
        }
    }

    /**
     * Menyembunyikan hologram dari pemain
     */
    public static void hideHologram(Hologram hologram, Player player) {
        if (hologram != null && player != null) {
            hologram.hide(player);
        }
    }

    /**
     * Menampilkan semua hologram ke pemain
     */
    public static void showAllHolograms(Player player) {
        getService().showAll(player);
    }

    /**
     * Menyembunyikan semua hologram dari pemain
     */
    public static void hideAllHolograms(Player player) {
        getService().hideAll(player);
    }

    // ==================== UPDATE HOLOGRAM ====================

    /**
     * Update hologram untuk pemain
     */
    public static void updateHologram(Hologram hologram, Player player) {
        if (hologram != null && player != null) {
            hologram.update(player);
        }
    }

    /**
     * Update hologram untuk semua pemain
     */
    public static void updateHologram(Hologram hologram) {
        if (hologram != null) {
            hologram.updateAll();
        }
    }

    /**
     * Update hologram berdasarkan nama
     */
    public static void updateHologram(String name) {
        Optional<Hologram> hologram = getService().getByName(name);
        if (hologram.isPresent()) {
            hologram.get().updateAll();
        }
    }

    // ==================== PAGE OPERATIONS ====================

    /**
     * Menambahkan page baru ke hologram
     */
    public static HologramPage addPage(Hologram hologram) {
        if (hologram == null) {
            throw new IllegalArgumentException("Hologram tidak boleh null");
        }
        return hologram.addPage();
    }

    /**
     * Menambahkan page pada index tertentu
     */
    public static HologramPage insertPage(Hologram hologram, int index) {
        if (hologram == null) {
            throw new IllegalArgumentException("Hologram tidak boleh null");
        }
        return hologram.insertPage(index);
    }

    /**
     * Menghapus page pada index tertentu
     */
    public static Optional<HologramPage> removePage(Hologram hologram, int index) {
        if (hologram == null) {
            throw new IllegalArgumentException("Hologram tidak boleh null");
        }
        return hologram.removePage(index);
    }

    /**
     * Mendapatkan page pada index tertentu
     */
    public static Optional<HologramPage> getPage(Hologram hologram, int index) {
        if (hologram == null) {
            throw new IllegalArgumentException("Hologram tidak boleh null");
        }
        return hologram.getPage(index);
    }

    // ==================== LINE OPERATIONS ====================

    /**
     * Menambahkan line ke page pertama hologram
     */
    public static HologramLine addLine(Hologram hologram, String content) {
        return addLine(hologram, 0, content);
    }

    /**
     * Menambahkan line ke page tertentu
     */
    public static HologramLine addLine(Hologram hologram, int pageIndex, String content) {
        if (hologram == null) {
            throw new IllegalArgumentException("Hologram tidak boleh null");
        }
        Optional<HologramPage> page = hologram.getPage(pageIndex);
        if (!page.isPresent()) {
            throw new IllegalArgumentException("Page index " + pageIndex + " tidak valid");
        }
        return page.get().addLine(content);
    }

    /**
     * Menambahkan line dengan tipe tertentu
     */
    public static HologramLine addLine(Hologram hologram, String content, HologramLineType type) {
        return addLine(hologram, 0, content, type);
    }

    /**
     * Menambahkan line dengan tipe tertentu ke page tertentu
     */
    public static HologramLine addLine(Hologram hologram, int pageIndex, String content, HologramLineType type) {
        if (hologram == null) {
            throw new IllegalArgumentException("Hologram tidak boleh null");
        }
        Optional<HologramPage> page = hologram.getPage(pageIndex);
        if (!page.isPresent()) {
            throw new IllegalArgumentException("Page index " + pageIndex + " tidak valid");
        }
        return page.get().addLine(content, type);
    }

    /**
     * Menambahkan line item ke hologram
     */
    public static HologramLine addItemLine(Hologram hologram, ItemStack item) {
        return addItemLine(hologram, 0, item);
    }

    /**
     * Menambahkan line item ke page tertentu
     */
    public static HologramLine addItemLine(Hologram hologram, int pageIndex, ItemStack item) {
        if (item == null) {
            throw new IllegalArgumentException("ItemStack tidak boleh null");
        }
        return addLine(hologram, pageIndex, item.getType().toString(), HologramLineType.ICON);
    }

    /**
     * Menghapus line pada index tertentu
     */
    public static Optional<HologramLine> removeLine(Hologram hologram, int lineIndex) {
        return removeLine(hologram, 0, lineIndex);
    }

    /**
     * Menghapus line pada index tertentu dari page tertentu
     */
    public static Optional<HologramLine> removeLine(Hologram hologram, int pageIndex, int lineIndex) {
        if (hologram == null) {
            throw new IllegalArgumentException("Hologram tidak boleh null");
        }
        Optional<HologramPage> page = hologram.getPage(pageIndex);
        if (!page.isPresent()) {
            throw new IllegalArgumentException("Page index " + pageIndex + " tidak valid");
        }
        return page.get().removeLine(lineIndex);
    }

    /**
     * Mendapatkan line pada index tertentu
     */
    public static Optional<HologramLine> getLine(Hologram hologram, int lineIndex) {
        return getLine(hologram, 0, lineIndex);
    }

    /**
     * Mendapatkan line pada index tertentu dari page tertentu
     */
    public static Optional<HologramLine> getLine(Hologram hologram, int pageIndex, int lineIndex) {
        if (hologram == null) {
            throw new IllegalArgumentException("Hologram tidak boleh null");
        }
        Optional<HologramPage> page = hologram.getPage(pageIndex);
        if (!page.isPresent()) {
            throw new IllegalArgumentException("Page index " + pageIndex + " tidak valid");
        }
        return page.get().getLine(lineIndex);
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Mengecek apakah hologram dengan nama tertentu sudah ada
     */
    public static boolean hologramExists(String name) {
        return getService().exists(name);
    }

    /**
     * Mengecek apakah hologram dengan UUID tertentu sudah ada
     */
    public static boolean hologramExists(UUID id) {
        return getService().exists(id);
    }

    /**
     * Mendapatkan jumlah hologram yang terdaftar
     */
    public static int getHologramCount() {
        return getService().getCount();
    }

    /**
     * Menyimpan semua hologram ke file
     */
    public static void saveAllHolograms() {
        getService().saveAll();
    }

    /**
     * Memuat semua hologram dari file
     */
    public static void loadAllHolograms() {
        getService().loadAll();
    }
}
