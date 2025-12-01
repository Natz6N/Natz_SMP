package com.natzsxn.mysmp.bootstrap;

import com.natzsxn.mysmp.Main;
import com.natzsxn.mysmp.commands.*;
import com.natzsxn.mysmp.config.ConfigManager;
import com.natzsxn.mysmp.listeners.*;
import com.natzsxn.mysmp.owner.OwnerManager;
import com.natzsxn.mysmp.npc.NpcManager;
import com.natzsxn.mysmp.holo.HologramManager;
import com.natzsxn.mysmp.service.WarpManager;
import com.natzsxn.mysmp.storage.DatabaseManager;
import com.natzsxn.mysmp.util.Messager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

/**
 * PluginBootstrap - Main initialization logic.
 * Sets up DI, database, commands, and listeners.
 */
public class PluginBootstrap {
    private final Main plugin;
    private final ServiceLocator services = new ServiceLocator();
    private DatabaseManager databaseManager;

    public PluginBootstrap(Main plugin) {
        this.plugin = plugin;
    }

    public void start() {
        // 1. Config
        ConfigManager configManager = new ConfigManager(plugin);
        configManager.loadAll();
        services.register(ConfigManager.class, configManager);

        // 2. Messager
        Messager messager = new Messager(plugin, configManager.getPrefix());
        services.register(Messager.class, messager);

        // 3. Database
        databaseManager = new DatabaseManager(plugin, messager, "database.sqlite");
        services.register(DatabaseManager.class, databaseManager);
        databaseManager.init();

        // 4. Warp Manager
        WarpManager warpManager = new WarpManager(plugin, configManager, databaseManager, messager);
        services.register(WarpManager.class, warpManager);

        // 4b. Owner Manager
        OwnerManager ownerManager = new OwnerManager(plugin, configManager, messager);
        services.register(OwnerManager.class, ownerManager);
        NpcManager npcManager = new NpcManager(plugin, configManager);
        HologramManager holoManager = new HologramManager(plugin, configManager);

        // 5. Commands
        plugin.getCommand("warp").setExecutor(new Command_Warp(warpManager, messager));
        plugin.getCommand("setlobbyspawn").setExecutor(new Command_SetSpawn(configManager, messager));
        plugin.getCommand("setsurvivalspawn").setExecutor(new Command_SetSpawn(configManager, messager));
        plugin.getCommand("setowner").setExecutor(new SetOwnerCommand(plugin, configManager, messager));
        plugin.getCommand("setlobby").setExecutor(new SetLobbyCommand(configManager, messager));
        plugin.getCommand("setspawn").setExecutor(new SetSpawnCommand(configManager, messager));
        plugin.getCommand("heal").setExecutor(new HealCommand(services));
        plugin.getCommand("reload").setExecutor(new ReloadCommand(services));
        plugin.getCommand("reloadowner").setExecutor(new ReloadOwnerCommand(ownerManager, messager));
        plugin.getCommand("reloadnpc").setExecutor(new ReloadNpcCommand(npcManager, holoManager, messager));
        plugin.getCommand("setlobbyworld").setExecutor(new SetLobbyWorldCommand(plugin, configManager, npcManager, holoManager, messager));
        plugin.getCommand("sayasetuju").setExecutor(new SayasetujuCommand(databaseManager, messager));
        Command_Owner ownerCmd = new Command_Owner(configManager, messager);
        ownerCmd.setOwnerManager(ownerManager);
        plugin.getCommand("owner").setExecutor(ownerCmd);

        // 6. Listeners
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new ProtectionListener(configManager, messager, ownerManager), plugin);
        pm.registerEvents(new PlayerJoinListener(plugin, configManager, databaseManager, messager), plugin);
        pm.registerEvents(new RulesBookListener(plugin, databaseManager, messager), plugin);
        pm.registerEvents(new OwnerListener(ownerManager), plugin);
        pm.registerEvents(npcManager, plugin);
        pm.registerEvents(new PlayerQuitListener(databaseManager), plugin);
        pm.registerEvents(new PlayerDeathListener(messager), plugin);
        pm.registerEvents(new WorldChangeListener(messager), plugin);
        
        Bukkit.getScheduler().runTask(plugin, () -> {
            npcManager.spawnAll();
            holoManager.spawnAll();
        });
        messager.info("Plugin bootstrapped successfully.");
    }

    public void stop() {
        if (databaseManager != null) {
            databaseManager.close();
        }
    }
}
