package com.natzdev.natzsmp.api.hologram.action;

import com.natzdev.natzsmp.api.hologram.HologramAction;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Action yang memainkan sound ketika hologram diklik.
 */
public class SoundAction implements HologramAction {
    private final Sound sound;
    private final float volume;
    private final float pitch;

    /**
     * Membuat SoundAction dengan volume dan pitch default
     */
    public SoundAction(Sound sound) {
        this(sound, 1.0f, 1.0f);
    }

    /**
     * Membuat SoundAction dengan volume dan pitch custom
     */
    public SoundAction(Sound sound, float volume, float pitch) {
        if (sound == null) {
            throw new IllegalArgumentException("Sound tidak boleh null");
        }
        if (volume < 0) {
            throw new IllegalArgumentException("Volume tidak boleh negatif");
        }
        if (pitch < 0.5f || pitch > 2.0f) {
            throw new IllegalArgumentException("Pitch harus antara 0.5 dan 2.0");
        }
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void execute(Player player) {
        if (player == null) {
            return;
        }
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    @Override
    public String getDescription() {
        return "SoundAction: " + sound.name() + " (volume: " + volume + ", pitch: " + pitch + ")";
    }

    @Override
    public String serialize() {
        return "SOUND:" + sound.name() + ":" + volume + ":" + pitch;
    }

    public Sound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }
}
