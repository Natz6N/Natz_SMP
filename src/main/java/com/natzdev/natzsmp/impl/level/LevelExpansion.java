package com.natzdev.natzsmp.impl.level;

import com.natzdev.natzsmp.NatzSMP;
import com.natzdev.natzsmp.api.level.LevelService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class LevelExpansion extends PlaceholderExpansion {
    private final NatzSMP plugin;
    private final LevelService levels;

    public LevelExpansion(NatzSMP plugin, LevelService levels) {
        this.plugin = plugin;
        this.levels = levels;
    }

    @Override
    public String getIdentifier() {
        return "natzsmp";
    }

    @Override
    public String getAuthor() {
        return "NatzSMP";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null || !player.isOnline()) return "";
        switch (params.toLowerCase()) {
            case "level":
                return String.valueOf(levels.getLevel(player.getPlayer()));
            case "xp":
                return String.valueOf(levels.getXp(player.getPlayer()));
            case "xp_required":
                return String.valueOf(levels.getXpRequired(player.getPlayer()));
            default:
                return "";
        }
    }
}
