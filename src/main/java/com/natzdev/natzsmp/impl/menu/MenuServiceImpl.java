package com.natzdev.natzsmp.impl.menu;

import com.natzdev.natzsmp.NatzSMP;
import com.natzdev.natzsmp.api.menu.MenuBuilder;
import com.natzdev.natzsmp.api.menu.MenuService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MenuServiceImpl implements MenuService {
    private final NatzSMP plugin;
    private static boolean LISTENER_REGISTERED = false;
    private static final Map<Inventory, Map<Integer, Consumer<Player>>> ACTIONS = new HashMap<>();

    public MenuServiceImpl(NatzSMP plugin) {
        this.plugin = plugin;
        if (!LISTENER_REGISTERED) {
            Bukkit.getPluginManager().registerEvents(new ClickListener(), plugin);
            LISTENER_REGISTERED = true;
        }
    }

    @Override
    public MenuBuilder builder(String title, int size) {
        return new BuilderImpl(title, size);
    }

    private static class Holder implements InventoryHolder {
        @Override public Inventory getInventory() { return null; }
    }

    private class BuilderImpl implements MenuBuilder {
        private final String title;
        private final int size;
        private final Map<Integer, Consumer<Player>> actions = new HashMap<>();
        private final Map<Integer, ItemStack> items = new HashMap<>();

        BuilderImpl(String title, int size) {
            this.title = title;
            this.size = Math.max(9, Math.min(54, size - size % 9));
        }

        @Override
        public MenuBuilder item(int slot, ItemStack item, Consumer<Player> onClick) {
            if (slot < 0 || slot >= size) return this;
            items.put(slot, item);
            actions.put(slot, onClick);
            return this;
        }

        @Override
        public void open(Player player) {
            Inventory inv = Bukkit.createInventory(new Holder(), size, title);
            for (Map.Entry<Integer, ItemStack> e : items.entrySet()) {
                inv.setItem(e.getKey(), e.getValue());
            }
            ACTIONS.put(inv, actions);
            player.openInventory(inv);
        }
    }

    private static class ClickListener implements Listener {
        @EventHandler
        public void onClick(InventoryClickEvent event) {
            Inventory inv = event.getInventory();
            if (ACTIONS.containsKey(inv)) {
                event.setCancelled(true);
                if (event.getWhoClicked() instanceof Player p && event.getCurrentItem() != null) {
                    var map = ACTIONS.get(inv);
                    var consumer = map.get(event.getRawSlot());
                    if (consumer != null) consumer.accept(p);
                }
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent event) {
            Inventory inv = event.getInventory();
            ACTIONS.remove(inv);
        }
    }
}
