// FILE: src/main/java/com/natzsxn/mysmp/bootstrap/PluginBootstrap.java
package com.natzsxn.mysmp.bootstrap;

import com.natzsxn.mysmp.Main;
import com.natzsxn.mysmp.commands.*;
import com.natzsxn.mysmp.config.ConfigManager;
import com.natzsxn.mysmp.listeners.*;
import com.natzsxn.mysmp.service.SqliteUserService;
import com.natzsxn.mysmp.storage.DatabaseManager;
import com.natzsxn.mysmp.storage.WorldDataStorage;
import com.natzsxn.mysmp.task.AutoSaveTask;
import com.natzsxn.mysmp.task.TeleportCooldownTask;
import com.natzsxn.mysmp.util.Messager;
import com.natzsxn.mysmp.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.util.concurrent.CompletableFuture;

public class PluginBootstrap {
    private final Main plugin;
    private final ServiceLocator services = new ServiceLocator();

    private AutoSaveTask autoSaveTask;
    private TeleportCooldownTask cooldownTask;
    
    public PluginBootstrap(Main plugin) {
        this.plugin = plugin;
    }

    public void start() {
        ConfigManager configManager = new ConfigManager(plugin);
        configManager.loadAll();
        services.register(ConfigManager.class, configManager);

        Messager messager = new Messager(configManager.getMessagesConfig().getPrefix());
        services.register(Messager.class, messager);

        DatabaseManager databaseManager = new DatabaseManager(plugin, "data.sqlite");
        services.register(DatabaseManager.class, databaseManager);

        WorldDataStorage worldDataStorage = new WorldDataStorage(databaseManager);
        services.register(WorldDataStorage.class, worldDataStorage);

        WorldManager worldManager = new WorldManager(plugin, worldDataStorage);
        services.register(WorldManager.class, worldManager);

        SqliteUserService userService = new SqliteUserService(databaseManager);
        services.register(SqliteUserService.class, userService);

        services.register(com.natzsxn.mysmp.service.TeleportService.class,
                new com.natzsxn.mysmp.service.TeleportService(worldDataStorage, new TeleportCooldownTask(), messager));

        registerEverything();

        CompletableFuture.runAsync(databaseManager::init)
                .thenCompose(v -> worldDataStorage.initSchema())
                .thenRun(() -> new WorldBootstrap(plugin, services).ensureWorlds())
                .exceptionally(ex -> { plugin.getLogger().severe("Async init error: " + ex.getMessage()); return null; });
    }

    private void registerEverything() {
        PluginManager pm = Bukkit.getPluginManager();

        CommandManager commandManager = new CommandManager(plugin);
        commandManager.register("heal", new HealCommand(services));
        commandManager.register("lobby", new LobbyCommand(services));
        commandManager.register("survival", new SurvivalCommand(services));
        commandManager.register("setlobby", new SetLobbyCommand(services));
        commandManager.register("setsurvival", new SetSurvivalCommand(services));
        commandManager.register("mysmp", new ReloadCommand(services));
        commandManager.register("sayasetuju", new com.natzsxn.mysmp.commands.SayasetujuCommand(plugin, services));

        pm.registerEvents(new PlayerJoinListener(plugin, services), plugin);
        pm.registerEvents(new BlockBreakListener(services), plugin);
        pm.registerEvents(new WorldChangeListener(services), plugin);
        pm.registerEvents(new LobbyProtectionListener(services), plugin);
        pm.registerEvents(new PlayerDeathListener(services), plugin);
        pm.registerEvents(new com.natzsxn.mysmp.listeners.RulesBookListener(plugin, services), plugin);

        cooldownTask = services.get(TeleportCooldownTask.class);

        autoSaveTask = new AutoSaveTask(services);
        autoSaveTask.start(plugin, 20L * 300); // 5 minutes
    }

    public void stop() {
        if (autoSaveTask != null) autoSaveTask.stop();
        DatabaseManager db = services.get(DatabaseManager.class);
        if (db != null) db.shutdown();
    }

    public ServiceLocator getServices() {
        return services;
    }
}
