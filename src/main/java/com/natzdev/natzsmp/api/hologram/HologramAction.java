package com.natzdev.natzsmp.api.hologram;

import org.bukkit.entity.Player;

/**
 * Interface untuk action yang dapat dijalankan ketika hologram diklik.
 * Action dapat berupa command execution, sound playing, atau custom logic.
 */
public interface HologramAction {
    /**
     * Menjalankan action untuk pemain yang mengklik hologram
     *
     * @param player Pemain yang mengklik
     */
    void execute(Player player);

    /**
     * Mendapatkan deskripsi action (untuk logging/debugging)
     *
     * @return Deskripsi action
     */
    String getDescription();

    /**
     * Mengkonversi action ke string format (untuk penyimpanan)
     *
     * @return String representation dari action
     */
    String serialize();
}
