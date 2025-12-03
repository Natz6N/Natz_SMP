package com.natzdev.natzsmp.api.economy;

import org.bukkit.entity.Player;

public interface EconomyService {
    boolean deposit(Player player, double amount);
    boolean withdraw(Player player, double amount);
    double getBalance(Player player);
}
