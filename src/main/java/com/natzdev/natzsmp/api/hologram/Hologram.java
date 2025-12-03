package com.natzdev.natzsmp.api.hologram;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Interface yang merepresentasikan hologram utama.
 * Hologram dapat memiliki multiple pages, dan setiap page memiliki multiple lines.
 */
public interface Hologram {
    /**
     * Mendapatkan UUID unik dari hologram
     */
    UUID getId();

    /**
     * Mendapatkan nama hologram
     */
    String getName();

    /**
     * Mendapatkan lokasi hologram
     */
    Location getLocation();

    /**
     * Mengatur lokasi hologram
     */
    void setLocation(Location location);

    /**
     * Mendapatkan semua pages dalam hologram
     */
    List<HologramPage> getPages();

    /**
     * Mendapatkan page pada index tertentu
     */
    Optional<HologramPage> getPage(int index);

    /**
     * Mendapatkan jumlah pages
     */
    int getPageCount();

    /**
     * Menambahkan page baru ke hologram
     */
    HologramPage addPage();

    /**
     * Menambahkan page pada index tertentu
     */
    HologramPage insertPage(int index);

    /**
     * Menghapus page pada index tertentu
     */
    Optional<HologramPage> removePage(int index);

    /**
     * Mendapatkan page pertama (convenience method)
     */
    HologramPage getFirstPage();

    /**
     * Menampilkan hologram ke pemain
     */
    void show(Player player);

    /**
     * Menyembunyikan hologram dari pemain
     */
    void hide(Player player);

    /**
     * Menampilkan hologram ke semua pemain
     */
    void showAll();

    /**
     * Menyembunyikan hologram dari semua pemain
     */
    void hideAll();

    /**
     * Update hologram untuk pemain
     */
    void update(Player player);

    /**
     * Update hologram untuk semua pemain
     */
    void updateAll();

    /**
     * Teleport hologram ke lokasi baru
     */
    void teleport(Location location);

    /**
     * Realign semua baris setelah perubahan lokasi
     */
    void realignLines();

    /**
     * Menghapus hologram
     */
    void delete();

    /**
     * Mengecek apakah hologram masih valid
     */
    boolean isValid();

    /**
     * Mengecek apakah hologram enabled
     */
    boolean isEnabled();

    /**
     * Mengatur enabled state
     */
    void setEnabled(boolean enabled);

    /**
     * Mendapatkan display range (jarak untuk menampilkan hologram)
     */
    int getDisplayRange();

    /**
     * Mengatur display range
     */
    void setDisplayRange(int range);

    /**
     * Mendapatkan update range (jarak untuk update hologram)
     */
    int getUpdateRange();

    /**
     * Mengatur update range
     */
    void setUpdateRange(int range);

    /**
     * Mendapatkan update interval (dalam ticks)
     */
    int getUpdateInterval();

    /**
     * Mengatur update interval
     */
    void setUpdateInterval(int interval);

    /**
     * Mengecek apakah hologram dimulai dari bawah (down origin)
     */
    boolean isDownOrigin();

    /**
     * Mengatur down origin
     */
    void setDownOrigin(boolean downOrigin);

    /**
     * Mengecek apakah hologram selalu menghadap ke pemain
     */
    boolean isAlwaysFacePlayer();

    /**
     * Mengatur always face player
     */
    void setAlwaysFacePlayer(boolean alwaysFacePlayer);

    /**
     * Mengatur permission untuk hologram (hanya pemain dengan permission ini yang bisa melihat)
     */
    void setPermission(String permission);

    /**
     * Mendapatkan permission dari hologram
     */
    String getPermission();

    /**
     * Menambahkan flag ke hologram
     */
    void addFlag(HologramFlag flag);

    /**
     * Menghapus flag dari hologram
     */
    void removeFlag(HologramFlag flag);

    /**
     * Mengecek apakah hologram memiliki flag tertentu
     */
    boolean hasFlag(HologramFlag flag);

    /**
     * Mendapatkan semua flag yang ada pada hologram
     */
    Set<HologramFlag> getFlags();

    /**
     * Menyimpan hologram ke file
     */
    void save();

    /**
     * Memuat hologram dari file
     */
    void load();

    /**
     * Mendapatkan hologram sebagai map untuk penyimpanan
     */
    Map<String, Object> serialize();

    /**
     * Backward compatibility - set lines untuk page pertama
     */
    @Deprecated
    void setLines(List<Component> lines);

    /**
     * Backward compatibility - get lines dari page pertama
     */
    @Deprecated
    List<Component> getLines();
}
