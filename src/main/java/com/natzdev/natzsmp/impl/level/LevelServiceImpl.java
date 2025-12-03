package com.natzdev.natzsmp.impl.level;

import com.natzdev.natzsmp.NatzSMP;
import com.natzdev.natzsmp.api.level.LevelService;
import org.bukkit.entity.Player;

import com.natzdev.natzsmp.storage.DatabaseManager;
import com.natzdev.natzsmp.storage.dao.LevelDao;
import com.natzdev.natzsmp.storage.dao.PlayerDao;
import com.natzdev.natzsmp.storage.dao.impl.LevelDaoImpl;
import com.natzdev.natzsmp.storage.dao.impl.PlayerDaoImpl;

import java.sql.Connection;
import java.sql.SQLException;

public class LevelServiceImpl implements LevelService {
    private final NatzSMP plugin;
    private final DatabaseManager db;
    private final PlayerDao playerDao;
    private final LevelDao levelDao;

    public LevelServiceImpl(NatzSMP plugin) {
        this.plugin = plugin;
        this.db = plugin.database();
        this.playerDao = new PlayerDaoImpl(db);
        this.levelDao = new LevelDaoImpl(db);
    }

    @Override
    public int getLevel(Player player) {
        try (Connection conn = db.getConnection()) {
            int pid = playerDao.ensurePlayer(conn, player);
            levelDao.ensureRow(conn, pid);
            return levelDao.getLevel(conn, pid);
        } catch (SQLException e) {
            plugin.getLogger().severe("getLevel failed: " + e.getMessage());
            return 1;
        }
    }

    @Override
    public int getXp(Player player) {
        try (Connection conn = db.getConnection()) {
            int pid = playerDao.ensurePlayer(conn, player);
            levelDao.ensureRow(conn, pid);
            return levelDao.getXp(conn, pid);
        } catch (SQLException e) {
            plugin.getLogger().severe("getXp failed: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public int getXpRequired(Player player) {
        int level = getLevel(player);
        try (Connection conn = db.getConnection()) {
            Integer req = levelDao.getRequiredXpForLevel(conn, level + 1);
            if (req != null) return req;
        } catch (SQLException e) {
            plugin.getLogger().warning("getXpRequired fallback: " + e.getMessage());
        }
        return Math.max(50, (level + 1) * 100);
    }

    @Override
    public void addXp(Player player, int amount) {
        int add = Math.max(0, amount);
        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);
            int pid = playerDao.ensurePlayer(conn, player);
            levelDao.ensureRow(conn, pid);
            int xp = levelDao.getXp(conn, pid) + add;
            int level = levelDao.getLevel(conn, pid);
            while (true) {
                Integer req = levelDao.getRequiredXpForLevel(conn, level + 1);
                int required = req != null ? req : Math.max(50, (level + 1) * 100);
                if (xp < required) break;
                xp -= required;
                level++;
            }
            levelDao.setLevel(conn, pid, level);
            levelDao.setXp(conn, pid, xp);
            conn.commit();
        } catch (SQLException e) {
            plugin.getLogger().severe("addXp failed: " + e.getMessage());
        }
    }

    @Override
    public void setLevel(Player player, int level) {
        try (Connection conn = db.getConnection()) {
            int pid = playerDao.ensurePlayer(conn, player);
            levelDao.ensureRow(conn, pid);
            levelDao.setLevel(conn, pid, Math.max(1, level));
        } catch (SQLException e) {
            plugin.getLogger().severe("setLevel failed: " + e.getMessage());
        }
    }

    @Override
    public void setXp(Player player, int xp) {
        try (Connection conn = db.getConnection()) {
            int pid = playerDao.ensurePlayer(conn, player);
            levelDao.ensureRow(conn, pid);
            levelDao.setXp(conn, pid, Math.max(0, xp));
        } catch (SQLException e) {
            plugin.getLogger().severe("setXp failed: " + e.getMessage());
        }
    }
}
