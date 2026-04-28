package com.parrot.lifesteal;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!e.getView().getTitle().equals("Revive Player")) return;

        e.setCancelled(true); // 🔒 prevents taking skulls

        if (e.getCurrentItem() == null) return;

        if (e.getCurrentItem().getType() != Material.PLAYER_HEAD) return;

        Player p = (Player) e.getWhoClicked();

        String name = ChatColor.stripColor(
                e.getCurrentItem().getItemMeta().getDisplayName()
        );

        OfflinePlayer target = Bukkit.getOfflinePlayer(name);

        if (!target.isBanned()) {
            p.sendMessage(ChatColor.RED + "Player is not banned!");
            return;
        }

        // ✅ Unban
        Bukkit.getBanList(BanList.Type.NAME).pardon(target.getName());

        // ✅ Restore hearts
        if (target.isOnline()) {
            Player t = target.getPlayer();
            HeartManager.set(t, 1);
        }

        // ✅ Remove beacon (both hands)
        if (p.getInventory().getItemInMainHand().getType() == Material.BEACON) {
            p.getInventory().setItemInMainHand(null);
        }

        if (p.getInventory().getItemInOffHand().getType() == Material.BEACON) {
            p.getInventory().setItemInOffHand(null);
        }

        p.sendMessage(ChatColor.GREEN + "Revived " + name + "!");
        p.closeInventory();
    }
}
