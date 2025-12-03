package com.natzdev.natzsmp.api.hologram;

/**
 * Enum untuk flag-flag yang dapat diterapkan pada hologram atau baris hologram.
 * Flags mengontrol perilaku dan tampilan hologram.
 */
public enum HologramFlag {
    /**
     * Flag untuk membuat hologram selalu menghadap ke pemain
     */
    ALWAYS_FACE_PLAYER("always_face_player"),

    /**
     * Flag untuk membuat hologram dimulai dari bawah (down origin)
     */
    DOWN_ORIGIN("down_origin"),

    /**
     * Flag untuk menonaktifkan shadow pada hologram
     */
    NO_SHADOW("no_shadow"),

    /**
     * Flag untuk membuat hologram tidak dapat diklik
     */
    NO_CLICK("no_click"),

    /**
     * Flag untuk membuat hologram tidak memiliki collision
     */
    NO_COLLISION("no_collision"),

    /**
     * Flag untuk membuat hologram transparan
     */
    TRANSPARENT("transparent"),

    /**
     * Flag untuk membuat hologram glowing
     */
    GLOWING("glowing"),

    /**
     * Flag untuk membuat hologram tidak terpengaruh oleh placeholder
     */
    NO_PLACEHOLDER("no_placeholder"),

    /**
     * Flag untuk membuat hologram tidak terpengaruh oleh animasi
     */
    NO_ANIMATION("no_animation");

    private final String id;

    HologramFlag(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    /**
     * Mendapatkan HologramFlag dari string ID
     */
    public static HologramFlag fromId(String id) {
        for (HologramFlag flag : values()) {
            if (flag.id.equalsIgnoreCase(id)) {
                return flag;
            }
        }
        return null;
    }
}
