package com.natzdev.natzsmp.api.hologram;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface untuk mengelola hologram.
 * Menyediakan metode untuk create, retrieve, update, dan delete hologram.
 */
public interface HologramService {
    /**
     * Membuat hologram baru dengan nama dan lokasi
     */
    Hologram create(String name, Location location);

    /**
     * Membuat hologram baru dengan nama, lokasi, dan lines
     */
    Hologram create(String name, Location location, List<Component> lines);

    /**
     * Membuat hologram baru dengan nama, lokasi, dan save to file option
     */
    Hologram create(String name, Location location, boolean saveToFile);

    /**
     * Membuat hologram baru dengan nama, lokasi, lines, dan save to file option
     */
    Hologram create(String name, Location location, List<Component> lines, boolean saveToFile);

    /**
     * Mendapatkan hologram berdasarkan UUID
     */
    Optional<Hologram> get(UUID id);

    /**
     * Mendapatkan hologram berdasarkan nama
     */
    Optional<Hologram> getByName(String name);

    /**
     * Mendapatkan semua hologram yang terdaftar
     */
    Collection<Hologram> getAll();

    /**
     * Menghapus hologram berdasarkan UUID
     */
    void remove(UUID id);

    /**
     * Menghapus hologram berdasarkan nama
     */
    void removeByName(String name);

    /**
     * Menghapus semua hologram
     */
    void removeAll();

    /**
     * Mengecek apakah hologram dengan nama tertentu sudah ada
     */
    boolean exists(String name);

    /**
     * Mengecek apakah hologram dengan UUID tertentu sudah ada
     */
    boolean exists(UUID id);

    /**
     * Mendapatkan jumlah hologram yang terdaftar
     */
    int getCount();

    /**
     * Menyimpan semua hologram ke file
     */
    void saveAll();

    /**
     * Memuat semua hologram dari file
     */
    void loadAll();

    /**
     * Menampilkan semua hologram ke pemain
     */
    void showAll(org.bukkit.entity.Player player);

    /**
     * Menyembunyikan semua hologram dari pemain
     */
    void hideAll(org.bukkit.entity.Player player);
}
