@EventHandler
public void onDeath(PlayerDeathEvent e) {

    Player dead = e.getEntity();
    Player killer = dead.getKiller();

    // ✅ Save inventory FIRST
    InventoryManager.save(
            dead.getUniqueId(),
            dead.getInventory().getContents(),
            dead.getInventory().getArmorContents()
    );

    // ❌ Stop Bukkit from controlling drops
    e.getDrops().clear();

    // ✅ Drop items manually
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

    // ✅ Clear inventory
    dead.getInventory().clear();
    dead.getInventory().setArmorContents(null);

    // ❤️ Heart logic
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

    // ☠️ BAN if 0 hearts
    if (h <= 0) {
        Bukkit.getBanList(BanList.Type.NAME)
                .addBan(dead.getName(), "Out of hearts", null, null);

        dead.kickPlayer("You lost all hearts!");
    }
}
