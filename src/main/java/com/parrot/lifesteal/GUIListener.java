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

        // ✅ Get player name from skull
        String name = ChatColor.stripColor(
                e.getCurrentItem().getItemMeta().getDisplayName()
        );

        // ✅ Check ban using modern API
        if (!Bukkit.getBanList(BanList.Type.NAME).isBanned(name)) {
            p.sendMessage(ChatColor.RED + "Player is not banned!");
            return;
        }

        // ✅ Unban player
        Bukkit.getBanList(BanList.Type.NAME).pardon(name);

        Player t = Bukkit.getPlayer(name);

        if (t != null) {

            // ❤️ Set 1 heart
            HeartManager.set(t, 1);

            // 🎒 Restore inventory
            ItemStack[] inv = InventoryManager.getInv(t.getUniqueId());
            ItemStack[] armor = InventoryManager.getArmor(t.getUniqueId());

            if (inv != null) {
                t.getInventory().setContents(inv);
            }

            if (armor != null) {
                t.getInventory().setArmorContents(armor);
            }

            InventoryManager.remove(t.getUniqueId());
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
