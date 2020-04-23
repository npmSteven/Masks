package net.stevenrafferty.headhunting.utils;

import net.stevenrafferty.headhunting.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.Iterator;

public class Helper {

    private Plugin plugin = Main.getPlugin(Main.class);

    public static String convertToInvisibleString(String s) {
        String hidden = "";
        for (char c : s.toCharArray()) hidden += ChatColor.COLOR_CHAR + "" + c;
        return hidden;
    }

    public static String convertToVisibleString(String s) {
        return s.replaceAll("§", "");
    }

    public static void removeHeads(Inventory inventory, ItemStack head, int amount) {
        if (amount <= 0) return;
        int size = inventory.getSize();
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null || !is.getType().equals(Material.SKULL_ITEM)) continue;
            SkullMeta isMeta = (SkullMeta) is.getItemMeta();
            if (isMeta.hasDisplayName() && headMeta.hasDisplayName()) {
                if (isMeta.getOwner().equals(headMeta.getOwner()) && isMeta.getDisplayName().equals(headMeta.getDisplayName())) {
                    int newAmount = is.getAmount() - amount;
                    if (newAmount > 0) {
                        is.setAmount(newAmount);
                        break;
                    } else {
                        inventory.clear(slot);
                        amount = -newAmount;
                        if (amount == 0) break;
                    }
                }
            }
        }
    }

    public static void removeTokens(Inventory inventory, ItemStack token, int amount) {
        if (amount <= 0) return;
        int size = inventory.getSize();
        ItemMeta tokenMeta = token.getItemMeta();
        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null || !is.getType().equals(Material.NETHER_STAR)) continue;
            ItemMeta isMeta = is.getItemMeta();
            if (isMeta.hasDisplayName() && tokenMeta.hasDisplayName()) {
                if (isMeta.getDisplayName().equals(tokenMeta.getDisplayName()) && isMeta.hasLore()) {
                    int newAmount = is.getAmount() - amount;
                    if (newAmount > 0) {
                        is.setAmount(newAmount);
                        break;
                    } else {
                        inventory.clear(slot);
                        amount = -newAmount;
                        if (amount == 0) break;
                    }
                }
            }
        }
    }

    public void remove(Material m) {
        Iterator<Recipe> it =  plugin.getServer().recipeIterator();
        Recipe recipe;
        while(it.hasNext())
        {
            recipe = it.next();
            if (recipe != null && recipe.getResult().getType() == m)
            {
                it.remove();
            }
        }
    }

    public String getConfigMessage(String path) {
        String message = plugin.getConfig().getString(path);
        if (message != null) {
            return plugin.getConfig().getString(path).replaceAll("(&([a-f0-9]))", "\u00A7$2");
        }
        return null;
    }

    public String[] getItemMetaInfo(ItemMeta itemMeta) {
        String firstLore = convertToVisibleString(itemMeta.getLore().get(0));
        String[] creatureLore = firstLore.split("_");
        return creatureLore;
    }

}
