// FILE: src/main/java/com/natzsxn/mysmp/Main.java
package com.natzsxn.mysmp;

import com.natzsxn.mysmp.bootstrap.PluginBootstrap;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private PluginBootstrap bootstrap;

    @Override
    public void onEnable() {
        bootstrap = new PluginBootstrap(this);
        bootstrap.start();
        getLogger().info("MySMPPlugin enabled");

        getLogger().info("Data folder: " + getDataFolder().getAbsolutePath());
        if (getDescription().getCommands() != null) {
            getLogger().info("plugin.yml commands: " + getDescription().getCommands().keySet());
            for (String cmd : getDescription().getCommands().keySet()) {
                if (getCommand(cmd) == null) {
                    getLogger().severe("getCommand('" + cmd + "') returned null. Check plugin.yml name.");
                } else {
                    getLogger().info("Command available: " + cmd);
                }
            }
        } else {
            getLogger().severe("No commands section found in plugin.yml");
        }
    }

    @Override
    public void onDisable() {
        if (bootstrap != null) bootstrap.stop();
        getLogger().info("MySMPPlugin disabled");
    }
}
