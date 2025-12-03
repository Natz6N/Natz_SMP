package com.natzdev.natzsmp.impl.economy;

import com.natzdev.natzsmp.NatzSMP;
import com.natzdev.natzsmp.api.economy.EconomyService;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;

import com.natzdev.natzsmp.storage.DatabaseManager;
import com.natzdev.natzsmp.storage.dao.PlayerDao;
import com.natzdev.natzsmp.storage.dao.EconomyDao;
import com.natzdev.natzsmp.storage.dao.TransactionDao;
import com.natzdev.natzsmp.storage.dao.impl.PlayerDaoImpl;
import com.natzdev.natzsmp.storage.dao.impl.EconomyDaoImpl;
import com.natzdev.natzsmp.storage.dao.impl.TransactionDaoImpl;

public class EconomyServiceImpl implements EconomyService {
    private final NatzSMP plugin;
    private Economy vault;
    private final DatabaseManager db;
    private final PlayerDao playerDao;
    private final EconomyDao economyDao;
    private final TransactionDao txDao;

    public EconomyServiceImpl(NatzSMP plugin) {
        this.plugin = plugin;
        this.db = plugin.database();
        this.playerDao = new PlayerDaoImpl(db);
        this.economyDao = new EconomyDaoImpl(db);
        this.txDao = new TransactionDaoImpl(db);
        tryHookVault();
    }

    private void tryHookVault() {
        if (!plugin.hasVault()) return;
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) vault = rsp.getProvider();
    }

    @Override
    public boolean deposit(Player player, double amount) {
        if (amount < 0) return false;
        if (vault != null) {
            return vault.depositPlayer(player, amount).transactionSuccess();
        }
        long delta = toCents(amount);
        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);
            int pid = playerDao.ensurePlayer(conn, player);
            long before = economyDao.getBalanceCents(conn, pid);
            long after = before + delta;
            economyDao.upsertBalanceCents(conn, pid, after);
            txDao.insert(conn, "deposit", null, pid, delta, "API deposit");
            conn.commit();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Deposit failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean withdraw(Player player, double amount) {
        if (amount < 0) return false;
        if (vault != null) {
            return vault.withdrawPlayer(player, amount).transactionSuccess();
        }
        long delta = toCents(amount);
        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);
            int pid = playerDao.ensurePlayer(conn, player);
            long before = economyDao.getBalanceCents(conn, pid);
            if (before < delta) { conn.rollback(); return false; }
            long after = before - delta;
            economyDao.upsertBalanceCents(conn, pid, after);
            txDao.insert(conn, "withdraw", pid, null, delta, "API withdraw");
            conn.commit();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Withdraw failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public double getBalance(Player player) {
        if (vault != null) return vault.getBalance(player);
        try (Connection conn = db.getConnection()) {
            int pid = playerDao.ensurePlayer(conn, player);
            long cents = economyDao.getBalanceCents(conn, pid);
            return fromCents(cents);
        } catch (SQLException e) {
            plugin.getLogger().severe("Get balance failed: " + e.getMessage());
            return 0.0;
        }
    }

    private static long toCents(double amount) {
        return BigDecimal.valueOf(amount).movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    private static double fromCents(long cents) {
        return BigDecimal.valueOf(cents, 2).doubleValue();
    }
}
