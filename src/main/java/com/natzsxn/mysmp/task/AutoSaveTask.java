// FILE: src/main/java/com/natzsxn/mysmp/task/AutoSaveTask.java
package com.natzsxn.mysmp.task;

import com.natzsxn.mysmp.bootstrap.ServiceLocator;
import com.natzsxn.mysmp.storage.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoSaveTask {
    private final ServiceLocator services;
    private int taskId = -1;

    public AutoSaveTask(ServiceLocator services) {
        this.services = services;
    }

    public void start(JavaPlugin plugin, long periodTicks) {
        taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            DatabaseManager db = services.get(DatabaseManager.class);
            // placeholder for periodic operations
            try { db.getConnection().close(); } catch (Exception ignored) {}
        }, periodTicks, periodTicks);
    }

    public void stop() {
        if (taskId != -1) {
            org.bukkit.Bukkit.getScheduler().cancelTask(taskId);
        }
    }
}
