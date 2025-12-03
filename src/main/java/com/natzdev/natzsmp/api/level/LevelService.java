package com.natzdev.natzsmp.api.level;

import org.bukkit.entity.Player;

public interface LevelService {
    int getLevel(Player player);
    int getXp(Player player);
    int getXpRequired(Player player);
    void addXp(Player player, int amount);
    void setLevel(Player player, int level);
    void setXp(Player player, int xp);
}
