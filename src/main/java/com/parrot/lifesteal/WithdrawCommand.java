
package com.parrot.lifesteal;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.command.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class WithdrawCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;

        int hearts = HeartManager.get(p);

        if (hearts <= 1) {
            p.sendMessage(ChatColor.RED + "You need at least 2 hearts!");
            return true;
        }

        HeartManager.set(p, hearts - 1);

        ItemStack heart = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = heart.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Heart");

        NamespacedKey key = new NamespacedKey(Main.getInstance(), "real_heart");
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);

        heart.setItemMeta(meta);

        p.getInventory().addItem(heart);

        p.sendMessage(ChatColor.GREEN + "You withdrew 1 heart!");
        return true;
    }
}
