package com.parrot.lifesteal;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        // ✅ Check correct GUI
        if (!e.getView().getTitle().equals("Revive Player")) return;

        e.setCancelled(true); // 🔒 Prevent taking items

        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType() != Material.PLAYER_HEAD) return;

        Player p = (Player) e.getWhoClicked();

        String name = ChatColor.stripColor(
                e.getCurrentItem().getItemMeta().getDisplayName()
        );

        // ❌ Not banned
        if (!Bukkit.getBanList(BanList.Type.NAME).isBanned(name)) {
            p.sendMessage(ChatColor.RED + "Player is not banned!");
            return;
        }

        // ✅ Unban
        Bukkit.getBanList(BanList.Type.NAME).pardon(name);

        Player t = Bukkit.getPlayer(name);

        if (t != null) {

            // ❤️ Give 1 heart
            HeartManager.set(t, 1);

            // 🔥 RESTORE INVENTORY (DUPE SYSTEM)
            ReviveStorage.restore(t);
        }

        // 🔥 Remove beacon (main hand)
        if (p.getInventory().getItemInMainHand().getType() == Material.BEACON) {
            p.getInventory().setItemInMainHand(null);
        }

        // 🔥 Remove beacon (offhand)
        if (p.getInventory().getItemInOffHand().getType() == Material.BEACON) {
            p.getInventory().setItemInOffHand(null);
        }

        p.sendMessage(ChatColor.GREEN + "Revived " + name + "!");
        p.closeInventory();
    }
}
