
package com.parrot.lifesteal;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.attribute.Attribute;

import java.util.*;

public class HeartManager {

    public static HashMap<UUID, Integer> hearts = new HashMap<>();

    public static int get(Player p) {
        return hearts.getOrDefault(p.getUniqueId(), 10);
    }

    public static void set(Player p, int h) {
        hearts.put(p.getUniqueId(), h);
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(h * 2);
    }

    public static void load(Main plugin) {
        FileConfiguration c = plugin.getConfig();
        if (c.contains("hearts")) {
            for (String key : c.getConfigurationSection("hearts").getKeys(false)) {
                hearts.put(UUID.fromString(key), c.getInt("hearts." + key));
            }
        }
    }

    public static void save(Main plugin) {
        FileConfiguration c = plugin.getConfig();
        for (UUID u : hearts.keySet()) {
            c.set("hearts." + u.toString(), hearts.get(u));
        }
        plugin.saveConfig();
    }
}
