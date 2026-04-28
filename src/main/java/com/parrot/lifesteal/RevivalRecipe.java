
package com.parrot.lifesteal;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class RevivalRecipe {

    public static ShapedRecipe getRecipe() {

        ItemStack beacon = new ItemStack(Material.BEACON);
        ItemMeta meta = beacon.getItemMeta();
        meta.setDisplayName("§bRevival Beacon");
        beacon.setItemMeta(meta);

        NamespacedKey key = new NamespacedKey(Main.getInstance(), "revival_beacon");

        ShapedRecipe recipe = new ShapedRecipe(key, beacon);

        recipe.shape("NNN", "NSN", "NNN");

        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        recipe.setIngredient('S', Material.NETHER_STAR);

        return recipe;
    }
}
