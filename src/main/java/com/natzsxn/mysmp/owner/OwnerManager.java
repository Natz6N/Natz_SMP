package com.natzsxn.mysmp.owner;

import com.natzsxn.mysmp.config.ConfigManager;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.Objects;
import java.util.UUID;

/**
 * OwnerManager: deteksi owner, integrasi LuckPerms bila tersedia,
 * dan penerapan efek kosmetik/crown.
 */
public class OwnerManager {
    private final ConfigManager config;
    private final Messager messager;
    private final Plugin plugin;

    public OwnerManager(Plugin plugin, ConfigManager config, Messager messager) {
        this.plugin = plugin;
        this.config = config;
        this.messager = messager;
    }

    public boolean isOwner(Player p) {
        String cfgUuid = config.getOwnerUUID();
        String cfgName = plugin.getConfig().getString("settings.owner-username", "");

        UUID u = p.getUniqueId();
        String name = p.getName();

        boolean byUuid = false;
        if (cfgUuid != null && !cfgUuid.trim().isEmpty()) {
            try {
                byUuid = u.equals(UUID.fromString(cfgUuid.trim()));
            } catch (IllegalArgumentException ignored) { byUuid = false; }
        }

        boolean byName = cfgName != null && !cfgName.isEmpty() && cfgName.equalsIgnoreCase(name);

        // LuckPerms sync: jika aktif, cek permission '*' atau group owner
        boolean lpOwner = false;
        try {
            if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
                lpOwner = p.hasPermission("*") || p.hasPermission("group.owner") || p.hasPermission("mysmp.owner");
            }
        } catch (Throwable t) {
            lpOwner = false;
        }

        boolean result = byUuid || byName || lpOwner;

        if (result) {
            Bukkit.getLogger().info("[Owner] Loaded: " + name + " (UUID valid=" + byUuid + ")");
        }
        return result;
    }

    public void applyOwnerEffects(Player p) {
        // Jangan override LP chat; gunakan scoreboard tag untuk nametag.
        ScoreboardManager sm = Bukkit.getScoreboardManager();
        if (sm == null) return;
        Scoreboard sb = sm.getMainScoreboard();
        Team team = sb.getTeam("mysmp_owner");
        if (team == null) {
            team = sb.registerNewTeam("mysmp_owner");
            team.setPrefix("§6✪ OWNER §f");
        } else {
            if (!Objects.equals(team.getPrefix(), "§6✪ OWNER §f")) {
                team.setPrefix("§6✪ OWNER §f");
            }
        }
        team.addEntry(p.getName());
        p.setScoreboard(sb);

        // Jika LuckPerms tidak aktif, berikan OP untuk memastikan full permission.
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
            if (!p.isOp()) {
                p.setOp(true);
                messager.info("Grant OP to owner (LuckPerms not active): " + p.getName());
            }
            // Chat fallback: prefix di displayName jika LP tidak aktif
            p.setDisplayName("§6✪ OWNER §f" + p.getName());
        }
    }

    public void clearOwnerEffects(Player p) {
        ScoreboardManager sm = Bukkit.getScoreboardManager();
        if (sm == null) return;
        Scoreboard sb = sm.getMainScoreboard();
        Team team = sb.getTeam("mysmp_owner");
        if (team != null) {
            team.removeEntry(p.getName());
        }
    }
}
