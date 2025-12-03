package com.natzdev.natzsmp.api.hologram.action;

import com.natzdev.natzsmp.api.hologram.HologramAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

/**
 * Action yang mengirim pesan ke pemain ketika hologram diklik.
 */
public class MessageAction implements HologramAction {
    private final String message;
    private final boolean useMiniMessage;

    /**
     * Membuat MessageAction dengan plain text message
     */
    public MessageAction(String message) {
        this(message, false);
    }

    /**
     * Membuat MessageAction dengan opsi untuk menggunakan MiniMessage format
     */
    public MessageAction(String message, boolean useMiniMessage) {
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Message tidak boleh null atau kosong");
        }
        this.message = message;
        this.useMiniMessage = useMiniMessage;
    }

    @Override
    public void execute(Player player) {
        if (player == null) {
            return;
        }

        String finalMessage = message.replace("%player%", player.getName());

        Component component;
        if (useMiniMessage) {
            component = MiniMessage.miniMessage().deserialize(finalMessage);
        } else {
            component = Component.text(finalMessage);
        }

        player.sendMessage(component);
    }

    @Override
    public String getDescription() {
        return "MessageAction: " + message;
    }

    @Override
    public String serialize() {
        return "MESSAGE:" + message + (useMiniMessage ? ":minimessage" : ":plain");
    }

    public String getMessage() {
        return message;
    }

    public boolean isUseMiniMessage() {
        return useMiniMessage;
    }
}
