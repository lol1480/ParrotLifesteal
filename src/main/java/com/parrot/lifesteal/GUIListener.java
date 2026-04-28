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

        e.setCancelled(true);

        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType() != Material.PLAYER_HEAD) return;

        Player p = (Player) e.getWhoClicked();

        String name = ChatColor.stripColor(
                e.getCurrentItem().getItemMeta().getDisplayName()
        );

        if (!Bukkit.getBanList(BanList.Type.NAME).isBanned(name)) {
            p.sendMessage(ChatColor.RED + "Player is not banned!");
            return;
        }

        Bukkit.getBanList(BanList.Type.NAME).pardon(name);

        Player t = Bukkit.getPlayer(name);

        if (t != null) {
            HeartManager.set(t, 1);

            if (InventoryManager.getInv(t.getUniqueId()) != null) {
                t.getInventory().setContents(
                        InventoryManager.getInv(t.getUniqueId())
                );
            }

            if (InventoryManager.getArmor(t.getUniqueId()) != null) {
                t.getInventory().setArmorContents(
                        InventoryManager.getArmor(t.getUniqueId())
                );
            }

            InventoryManager.remove(t.getUniqueId());
        }

        // Remove beacon
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
