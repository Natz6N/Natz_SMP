package com.natzdev.natzsmp.api.hologram;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.UUID;

/**
 * Interface yang merepresentasikan satu baris dalam hologram.
 * Setiap baris dapat berisi teks, item, atau entity.
 */
public interface HologramLine {
    /**
     * Mendapatkan UUID unik dari baris
     */
    UUID getId();

    /**
     * Mendapatkan parent page dari baris ini
     */
    HologramPage getParent();

    /**
     * Mendapatkan tipe baris (TEXT, ICON, ENTITY, dll)
     */
    HologramLineType getType();

    /**
     * Mendapatkan lokasi baris
     */
    Location getLocation();

    /**
     * Mengatur lokasi baris
     */
    void setLocation(Location location);

    /**
     * Mendapatkan konten baris (text, item name, entity type, dll)
     */
    String getContent();

    /**
     * Mengatur konten baris
     */
    void setContent(String content);

    /**
     * Mendapatkan teks yang ditampilkan (untuk TEXT type)
     */
    Component getText();

    /**
     * Mengatur teks yang ditampilkan
     */
    void setText(Component text);

    /**
     * Mendapatkan item yang ditampilkan (untuk ICON type)
     */
    ItemStack getItem();

    /**
     * Mengatur item yang ditampilkan
     */
    void setItem(ItemStack item);

    /**
     * Mendapatkan tinggi baris (jarak antar baris)
     */
    double getHeight();

    /**
     * Mengatur tinggi baris
     */
    void setHeight(double height);

    /**
     * Mendapatkan offset X dari baris
     */
    double getOffsetX();

    /**
     * Mengatur offset X
     */
    void setOffsetX(double offsetX);

    /**
     * Mendapatkan offset Y dari baris
     */
    double getOffsetY();

    /**
     * Mengatur offset Y
     */
    void setOffsetY(double offsetY);

    /**
     * Mendapatkan offset Z dari baris
     */
    double getOffsetZ();

    /**
     * Mengatur offset Z
     */
    void setOffsetZ(double offsetZ);

    /**
     * Mengatur semua offset sekaligus
     */
    void setOffset(double x, double y, double z);

    /**
     * Mendapatkan facing/rotation dari baris (dalam derajat)
     */
    float getFacing();

    /**
     * Mengatur facing/rotation
     */
    void setFacing(float facing);

    /**
     * Menambahkan flag ke baris
     */
    void addFlag(HologramFlag flag);

    /**
     * Menghapus flag dari baris
     */
    void removeFlag(HologramFlag flag);

    /**
     * Mengecek apakah baris memiliki flag tertentu
     */
    boolean hasFlag(HologramFlag flag);

    /**
     * Mendapatkan semua flag yang ada pada baris
     */
    Set<HologramFlag> getFlags();

    /**
     * Mengatur permission untuk baris (hanya pemain dengan permission ini yang bisa melihat)
     */
    void setPermission(String permission);

    /**
     * Mendapatkan permission dari baris
     */
    String getPermission();

    /**
     * Menampilkan baris ke pemain
     */
    void show(Player player);

    /**
     * Menyembunyikan baris dari pemain
     */
    void hide(Player player);

    /**
     * Update baris untuk pemain (refresh tampilan)
     */
    void update(Player player);

    /**
     * Update baris untuk semua pemain yang melihatnya
     */
    void updateAll();

    /**
     * Menghapus baris
     */
    void delete();

    /**
     * Mengecek apakah baris masih valid/aktif
     */
    boolean isValid();

    /**
     * Mendapatkan baris sebagai map untuk penyimpanan
     */
    java.util.Map<String, Object> serialize();
}
