package com.natzdev.natzsmp;

import com.natzdev.natzsmp.api.economy.EconomyService;
import com.natzdev.natzsmp.api.hologram.HologramService;
import com.natzdev.natzsmp.api.level.LevelService;
import com.natzdev.natzsmp.api.menu.MenuService;
import com.natzdev.natzsmp.api.npc.NpcService;
import com.natzdev.natzsmp.impl.economy.EconomyServiceImpl;
import com.natzdev.natzsmp.impl.hologram.HologramServiceImpl;
import com.natzdev.natzsmp.impl.level.LevelServiceImpl;
import com.natzdev.natzsmp.impl.menu.MenuServiceImpl;
import com.natzdev.natzsmp.impl.npc.NpcServiceImpl;
import com.natzdev.natzsmp.listeners.PlayerJoinQuitListener;
import com.natzdev.natzsmp.nms.NmsAdapter;
import com.natzdev.natzsmp.nms.PaperAdapter;
import com.natzdev.natzsmp.storage.ConfigStore;
import com.natzdev.natzsmp.storage.DatabaseManager;
import com.natzdev.natzsmp.util.ServiceRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class NatzSMP extends JavaPlugin {
    private static NatzSMP instance;

    private boolean hasPapi;
    private boolean hasVault;
    private boolean hasLuckPerms;

    private ConfigStore configStore;
    private DatabaseManager databaseManager;

    public static NatzSMP get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // Ensure data folder and default resources
        saveDefaultConfig();
        ensureResource("messages.yml");
        ensureResource("holograms.yml");
        ensureResource("npcs.yml");
        ensureResource("players.yml");
        ensureResource("economy.yml");

        this.configStore = new ConfigStore(this);

        // Initialize SQLite database (file-based) and run migrations
        this.databaseManager = new DatabaseManager(this);
        this.databaseManager.init();

        // Detect soft-dependencies
        this.hasPapi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        this.hasVault = Bukkit.getPluginManager().getPlugin("Vault") != null;
        this.hasLuckPerms = Bukkit.getPluginManager().getPlugin("LuckPerms") != null;

        // Register NMS adapter (Paper API only, no raw NMS)
        NmsAdapter nms = new PaperAdapter(this);
        ServiceRegistry.register(NmsAdapter.class, nms);
        // Also register API adapter interface for renderer factory usage
        ServiceRegistry.register(com.natzdev.natzsmp.nms.api.NmsAdapter.class, (com.natzdev.natzsmp.nms.api.NmsAdapter) nms);

        // Register services
        ServiceRegistry.register(HologramService.class, new HologramServiceImpl(this));
        ServiceRegistry.register(NpcService.class, new NpcServiceImpl(this));
        ServiceRegistry.register(MenuService.class, new MenuServiceImpl(this));
        ServiceRegistry.register(LevelService.class, new LevelServiceImpl(this));
        ServiceRegistry.register(EconomyService.class, new EconomyServiceImpl(this));

        // PlaceholderAPI expansion
        if (hasPapi) {
            try {
                new com.natzdev.natzsmp.impl.level.LevelExpansion(this, ServiceRegistry.get(LevelService.class)).register();
            } catch (Throwable ignored) {}
        }

        // Listeners
        Bukkit.getPluginManager().registerEvents(new PlayerJoinQuitListener(), this);

        // Commands
        if (getCommand("natz") != null) {
            getCommand("natz").setExecutor(new com.natzdev.natzsmp.commands.NatzCommand());
            getCommand("natz").setTabCompleter(new com.natzdev.natzsmp.commands.NatzCommand());
        }

        getLogger().info("NatzSMP enabled. PAPI=" + hasPapi + ", Vault=" + hasVault + ", LuckPerms=" + hasLuckPerms);
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        ServiceRegistry.clear();
        getLogger().info("NatzSMP disabled");
    }

    public boolean hasPapi() { return hasPapi; }
    public boolean hasVault() { return hasVault; }
    public boolean hasLuckPerms() { return hasLuckPerms; }

    public ConfigStore configs() { return configStore; }

    public DatabaseManager database() { return databaseManager; }

    private void ensureResource(String name) {
        File out = new File(getDataFolder(), name);
        if (!out.exists()) {
            saveResource(name, false);
        }
    }
}
