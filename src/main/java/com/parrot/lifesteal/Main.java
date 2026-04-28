
package com.parrot.lifesteal;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        HeartManager.load(this);

        getServer().getPluginManager().registerEvents(new HeartListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new RevivalListener(), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);

        getCommand("withdraw").setExecutor(new WithdrawCommand());
    }

    @Override
    public void onDisable() {
        HeartManager.save(this);
    }

    public static Main getInstance() {
        return instance;
    }
}
