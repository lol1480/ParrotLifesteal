package com.parrot.lifesteal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RevivalListener implements Listener {

    @EventHandler
    public void onUse(PlayerInteractEvent e) {

        Player p = e.getPlayer();

        if (e.getItem() == null) return;

        ItemStack item = e.getItem();

        if (item.getType() != Material.BEACON) return;

        if (!item.hasItemMeta()) return;

        if (!item.getItemMeta().getDisplayName().equals("§bRevival Beacon")) return;

        // ✅ Open GUI
        Inventory gui = Bukkit.createInventory(null, 27, "Revive Player");

        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if (!op.isBanned()) continue;

            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta meta = skull.getItemMeta();
            meta.setDisplayName(ChatColor.RED + op.getName());
            skull.setItemMeta(meta);

            gui.addItem(skull);
        }

        p.openInventory(gui);
    }
}
