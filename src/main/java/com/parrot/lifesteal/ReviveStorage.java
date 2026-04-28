package com.parrot.lifesteal;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class ReviveStorage {

    private static final HashMap<UUID, ItemStack[]> stored = new HashMap<>();

    public static void save(Player p) {
        stored.put(p.getUniqueId(), p.getInventory().getContents());
    }

    public static void restore(Player p) {
        if (!stored.containsKey(p.getUniqueId())) return;

        p.getInventory().setContents(stored.get(p.getUniqueId()));
        stored.remove(p.getUniqueId());
    }
}
