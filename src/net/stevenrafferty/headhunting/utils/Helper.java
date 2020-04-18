package net.stevenrafferty.headhunting.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Helper {

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
