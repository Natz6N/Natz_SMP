package com.natzdev.natzsmp.api.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface MenuBuilder {
    MenuBuilder item(int slot, ItemStack item, Consumer<Player> onClick);
    void open(Player player);
}
