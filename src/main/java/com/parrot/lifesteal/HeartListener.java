package com.parrot.lifesteal;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.UUID;

public class HeartListener implements Listener {

    private final int MAX_HEARTS = 20;

    private final HashMap<UUID, Long> cooldown = new HashMap<>();
    private final long COOLDOWN_TIME = 5000;

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

        // ⏱️ cooldown
        if (cooldown.containsKey(p.getUniqueId())) {
            long last = cooldown.get(p.getUniqueId());
            if (System.currentTimeMillis() - last < COOLDOWN_TIME) {
                p.sendMessage(ChatColor.RED + "Wait before using another heart!");
                return;
            }
        }

        int current = HeartManager.get(p);
        if (current >= MAX_HEARTS) {
            p.sendMessage(ChatColor.RED + "You are at max hearts!");
            return;
        }

        HeartManager.set(p, current + 1);
        item.setAmount(item.getAmount() - 1);

        cooldown.put(p.getUniqueId(), System.currentTimeMillis());

        p.sendMessage(ChatColor.GREEN + "+1 Heart!");
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        Player dead = e.getEntity();
        Player killer = dead.getKiller();

        // ❗ ONLY run lifesteal if killed by player
        if (killer != null) {

            // 💾 save inventory (dupe system)
            ReviveStorage.save(dead);

            // ❤️ remove heart
            int newHearts = HeartManager.get(dead) - 1;
            HeartManager.set(dead, newHearts);

            // 🗡️ killer reward
            if (HeartManager.get(killer) >= MAX_HEARTS) {

                ItemStack heart = new ItemStack(Material.NETHER_STAR);
                ItemMeta meta = heart.getItemMeta();
                meta.setDisplayName(ChatColor.RED + "Heart");

                NamespacedKey key = new NamespacedKey(Main.getInstance(), "real_heart");
                meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);

                heart.setItemMeta(meta);
                killer.getInventory().addItem(heart);

            } else {
                HeartManager.set(killer,
                        Math.min(HeartManager.get(killer) + 1, MAX_HEARTS));
            }

            // ☠️ ban at 0 hearts
            if (newHearts <= 0) {
                Bukkit.getBanList(BanList.Type.NAME)
                        .addBan(dead.getName(), "Out of hearts", null, null);

                dead.kickPlayer(ChatColor.RED + "You lost all hearts!");
            }

        } else {
            // 🧠 death by zombie / fall / lava
            dead.sendMessage(ChatColor.YELLOW + "No hearts lost (not killed by player)");
        }
    }
}
