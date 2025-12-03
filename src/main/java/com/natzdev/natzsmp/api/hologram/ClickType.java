package com.natzdev.natzsmp.api.hologram;

/**
 * Enum untuk tipe-tipe klik yang dapat ditangani oleh hologram.
 */
public enum ClickType {
    /**
     * Klik kiri (left click)
     */
    LEFT("left"),

    /**
     * Klik kanan (right click)
     */
    RIGHT("right"),

    /**
     * Klik tengah (middle click)
     */
    MIDDLE("middle"),

    /**
     * Shift + klik kiri
     */
    SHIFT_LEFT("shift_left"),

    /**
     * Shift + klik kanan
     */
    SHIFT_RIGHT("shift_right");

    private final String id;

    ClickType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    /**
     * Mendapatkan ClickType dari string ID
     */
    public static ClickType fromId(String id) {
        for (ClickType type : values()) {
            if (type.id.equalsIgnoreCase(id)) {
                return type;
            }
        }
        return LEFT; // Default ke LEFT jika tidak ditemukan
    }
}
