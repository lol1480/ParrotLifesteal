package com.parrot.lifesteal;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ReviveStorage {

    private static final HashMap<UUID, ItemStack[]> stored = new HashMap<>();
    private static final HashSet<UUID> pending = new HashSet<>();

    public static void save(Player p) {
        stored.put(p.getUniqueId(), p.getInventory().getContents());
    }

    public static void restore(Player p) {
        if (!stored.containsKey(p.getUniqueId())) return;

        p.getInventory().setContents(stored.get(p.getUniqueId()));
        stored.remove(p.getUniqueId());
    }

    public static void mark(UUID uuid) {
        pending.add(uuid);
    }

    public static boolean hasPending(UUID uuid) {
        return pending.contains(uuid);
    }

    public static void remove(UUID uuid) {
        pending.remove(uuid);
    }
}
