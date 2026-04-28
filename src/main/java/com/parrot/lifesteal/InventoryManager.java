package com.parrot.lifesteal;

import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.UUID;

public class InventoryManager {

    private static final HashMap<UUID, ItemStack[]> inventories = new HashMap<>();
    private static final HashMap<UUID, ItemStack[]> armors = new HashMap<>();

    public static void save(UUID uuid, ItemStack[] inv, ItemStack[] armor) {
        inventories.put(uuid, inv);
        armors.put(uuid, armor);
    }

    public static ItemStack[] getInv(UUID uuid) {
        return inventories.get(uuid);
    }

    public static ItemStack[] getArmor(UUID uuid) {
        return armors.get(uuid);
    }

    public static void remove(UUID uuid) {
        inventories.remove(uuid);
        armors.remove(uuid);
    }
}
