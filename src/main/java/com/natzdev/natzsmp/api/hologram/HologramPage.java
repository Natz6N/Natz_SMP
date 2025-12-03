package com.natzdev.natzsmp.api.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Interface yang merepresentasikan satu halaman dalam hologram.
 * Setiap halaman dapat berisi multiple baris dan memiliki click actions tersendiri.
 */
public interface HologramPage {
    /**
     * Mendapatkan index halaman (0-based)
     */
    int getIndex();

    /**
     * Mendapatkan parent hologram dari halaman ini
     */
    Hologram getParent();

    /**
     * Mendapatkan semua baris dalam halaman
     */
    List<HologramLine> getLines();

    /**
     * Mendapatkan baris pada index tertentu
     */
    Optional<HologramLine> getLine(int index);

    /**
     * Mendapatkan jumlah baris dalam halaman
     */
    int getLineCount();

    /**
     * Menambahkan baris ke halaman
     */
    HologramLine addLine(String content);

    /**
     * Menambahkan baris dengan tipe tertentu
     */
    HologramLine addLine(String content, HologramLineType type);

    /**
     * Menambahkan baris pada index tertentu
     */
    HologramLine insertLine(int index, String content);

    /**
     * Menambahkan baris pada index tertentu dengan tipe tertentu
     */
    HologramLine insertLine(int index, String content, HologramLineType type);

    /**
     * Menghapus baris pada index tertentu
     */
    Optional<HologramLine> removeLine(int index);

    /**
     * Menghapus semua baris
     */
    void clearLines();

    /**
     * Mendapatkan tinggi total halaman (sum dari semua tinggi baris)
     */
    double getTotalHeight();

    /**
     * Mendapatkan lokasi center dari halaman
     */
    Location getCenter();

    /**
     * Mendapatkan lokasi untuk baris berikutnya
     */
    Location getNextLineLocation();

    /**
     * Menambahkan click action untuk halaman
     */
    void addClickAction(ClickType clickType, HologramAction action);

    /**
     * Menghapus click action
     */
    void removeClickAction(ClickType clickType, HologramAction action);

    /**
     * Mendapatkan semua click actions untuk tipe klik tertentu
     */
    List<HologramAction> getClickActions(ClickType clickType);

    /**
     * Menjalankan semua actions untuk tipe klik tertentu
     */
    void executeClickActions(ClickType clickType, Player player);

    /**
     * Menambahkan flag ke halaman
     */
    void addFlag(HologramFlag flag);

    /**
     * Menghapus flag dari halaman
     */
    void removeFlag(HologramFlag flag);

    /**
     * Mengecek apakah halaman memiliki flag tertentu
     */
    boolean hasFlag(HologramFlag flag);

    /**
     * Mendapatkan semua flag yang ada pada halaman
     */
    Set<HologramFlag> getFlags();

    /**
     * Menampilkan halaman ke pemain
     */
    void show(Player player);

    /**
     * Menyembunyikan halaman dari pemain
     */
    void hide(Player player);

    /**
     * Update halaman untuk pemain
     */
    void update(Player player);

    /**
     * Update halaman untuk semua pemain
     */
    void updateAll();

    /**
     * Menghapus halaman
     */
    void delete();

    /**
     * Mengecek apakah halaman masih valid
     */
    boolean isValid();

    /**
     * Mendapatkan halaman sebagai map untuk penyimpanan
     */
    Map<String, Object> serialize();
}
