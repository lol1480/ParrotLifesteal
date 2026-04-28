
package com.parrot.lifesteal;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.addRecipe(RevivalRecipe.getRecipe());
    }

    public static Main getInstance() {
        return instance;
    }
}
