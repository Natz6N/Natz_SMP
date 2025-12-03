package com.natzdev.natzsmp.api.hologram.action;

import com.natzdev.natzsmp.api.hologram.HologramAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Action yang menjalankan command ketika hologram diklik.
 */
public class CommandAction implements HologramAction {
    private final String command;
    private final boolean asConsole;

    /**
     * Membuat CommandAction yang dijalankan oleh pemain
     */
    public CommandAction(String command) {
        this(command, false);
    }

    /**
     * Membuat CommandAction dengan opsi untuk dijalankan sebagai console
     */
    public CommandAction(String command, boolean asConsole) {
        if (command == null || command.isEmpty()) {
            throw new IllegalArgumentException("Command tidak boleh null atau kosong");
        }
        this.command = command;
        this.asConsole = asConsole;
    }

    @Override
    public void execute(Player player) {
        if (player == null) {
            return;
        }

        String finalCommand = command.replace("%player%", player.getName());

        if (asConsole) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
        } else {
            player.performCommand(finalCommand);
        }
    }

    @Override
    public String getDescription() {
        return "CommandAction: " + command + (asConsole ? " (as console)" : " (as player)");
    }

    @Override
    public String serialize() {
        return "COMMAND:" + command + (asConsole ? ":console" : ":player");
    }

    public String getCommand() {
        return command;
    }

    public boolean isAsConsole() {
        return asConsole;
    }
}
