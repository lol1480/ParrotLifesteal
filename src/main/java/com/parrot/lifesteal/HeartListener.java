package com.parrot.lifesteal;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class HeartListener implements Listener {

    private final int MAX_HEARTS = 20;

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        if (e.getItem() == null) return;

        ItemStack item = e.getItem();
        if (item.getType() != Material.NETHER_STAR) return;
        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(Main.getInstance(), "real_heart");

        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) return;

        Player p = e.getPlayer();

        int current = HeartManager.get(p);
        if (current >= MAX_HEARTS) {
            p.sendMessage(ChatColor.RED + "You are at max hearts!");
            return;
        }

        HeartManager.set(p, current + 1);
        item.setAmount(item.getAmount() - 1);

        p.sendMessage(ChatColor.GREEN + "+1 Heart!");
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        Player dead = e.getEntity();
        Player killer = dead.getKiller();

        // ✅ Save inventory
        InventoryManager.save(
                dead.getUniqueId(),
                dead.getInventory().getContents(),
                dead.getInventory().getArmorContents()
        );

        // ❌ Clear default drops
        e.getDrops().clear();

        // ✅ Drop manually
        for (ItemStack item : dead.getInventory().getContents()) {
            if (item != null) {
                dead.getWorld().dropItemNaturally(dead.getLocation(), item);
            }
        }

        for (ItemStack item : dead.getInventory().getArmorContents()) {
            if (item != null) {
                dead.getWorld().dropItemNaturally(dead.getLocation(), item);
            }
        }

        dead.getInventory().clear();
        dead.getInventory().setArmorContents(null);

        // ❤️ Heart system
        int h = HeartManager.get(dead) - 1;
        HeartManager.set(dead, h);

        if (killer != null) {
            if (HeartManager.get(killer) >= MAX_HEARTS) {

                ItemStack heart = new ItemStack(Material.NETHER_STAR);
                ItemMeta meta = heart.getItemMeta();
                meta.setDisplayName(ChatColor.RED + "Heart");

                NamespacedKey key = new NamespacedKey(Main.getInstance(), "real_heart");
                meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);

                heart.setItemMeta(meta);
                killer.getInventory().addItem(heart);

            } else {
                HeartManager.set(killer, Math.min(HeartManager.get(killer) + 1, MAX_HEARTS));
            }
        }

        // ☠️ Ban at 0 hearts
        if (h <= 0) {
            Bukkit.getBanList(BanList.Type.NAME)
                    .addBan(dead.getName(), "Out of hearts", null, null);

            dead.kickPlayer("You lost all hearts!");
        }
    }
}
