package com.parrot.lifesteal;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class WithdrawCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;

        if (args.length == 0) {
            p.sendMessage(ChatColor.RED + "Usage: /withdraw <amount>");
            return true;
        }

        int amount;

        try {
            amount = Integer.parseInt(args[0]);
        } catch (Exception e) {
            p.sendMessage(ChatColor.RED + "Invalid number!");
            return true;
        }

        if (amount <= 0) {
            p.sendMessage(ChatColor.RED + "Must be positive!");
            return true;
        }

        int hearts = HeartManager.get(p);

        if (hearts - amount < 1) {
            p.sendMessage(ChatColor.RED + "You must keep at least 1 heart!");
            return true;
        }

        HeartManager.set(p, hearts - amount);

        for (int i = 0; i < amount; i++) {
            ItemStack heart = new ItemStack(Material.NETHER_STAR);
            ItemMeta meta = heart.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "Heart");

            NamespacedKey key = new NamespacedKey(Main.getInstance(), "real_heart");
            meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);

            heart.setItemMeta(meta);
            p.getInventory().addItem(heart);
        }

        p.sendMessage(ChatColor.GREEN + "Withdrew " + amount + " hearts!");

        return true;
    }
}
