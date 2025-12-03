package com.natzdev.natzsmp.api.hologram;

/**
 * Enum yang merepresentasikan tipe-tipe baris hologram yang berbeda.
 * Setiap tipe memiliki cara rendering dan interaksi yang unik.
 */
public enum HologramLineType {
    /**
     * Text line - menampilkan teks dengan format
     */
    TEXT("text"),

    /**
     * Icon line - menampilkan item stack sebagai hologram
     */
    ICON("icon"),

    /**
     * Entity line - menampilkan entity (armor stand, dll)
     */
    ENTITY("entity"),

    /**
     * Head line - menampilkan kepala pemain dengan ukuran normal
     */
    HEAD("head"),

    /**
     * Small head line - menampilkan kepala pemain dengan ukuran kecil
     */
    SMALL_HEAD("small_head");

    private final String id;

    HologramLineType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    /**
     * Mendapatkan HologramLineType dari string ID
     */
    public static HologramLineType fromId(String id) {
        for (HologramLineType type : values()) {
            if (type.id.equalsIgnoreCase(id)) {
                return type;
            }
        }
        return TEXT; // Default ke TEXT jika tidak ditemukan
    }
}
