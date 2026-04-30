package com.parrot.lifesteal;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!ChatColor.stripColor(e.getView().getTitle()).equalsIgnoreCase("Revive Player")) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType() != Material.PLAYER_HEAD) return;

        Player p = (Player) e.getWhoClicked();

        String name = ChatColor.stripColor(
                e.getCurrentItem().getItemMeta().getDisplayName()
        );

        // ❌ not banned
        if (!Bukkit.getBanList(BanList.Type.NAME).isBanned(name)) {
            p.sendMessage(ChatColor.RED + "Not banned!");
            return;
        }

        // ✅ UNBAN
        Bukkit.getBanList(BanList.Type.NAME).pardon(name);

        OfflinePlayer target = Bukkit.getOfflinePlayer(name);

        if (target.isOnline()) {
            Player t = target.getPlayer();

            HeartManager.set(t, 1);
            ReviveStorage.restore(t);

        } else {
            // 🔥 FIX: restore when they join
            ReviveStorage.mark(target.getUniqueId());
        }

       ItemStack main = p.getInventory().getItemInMainHand();
ItemStack off = p.getInventory().getItemInOffHand();

// ✅ remove ONLY ONE beacon
if (main.getType() == Material.BEACON) {
    if (main.getAmount() > 1) {
        main.setAmount(main.getAmount() - 1);
    } else {
        p.getInventory().setItemInMainHand(null);
    }
} else if (off.getType() == Material.BEACON) {
    if (off.getAmount() > 1) {
        off.setAmount(off.getAmount() - 1);
    } else {
        p.getInventory().setItemInOffHand(null);
    }
}

        p.sendMessage(ChatColor.GREEN + "Revived " + name);
        p.closeInventory();
    }
}
