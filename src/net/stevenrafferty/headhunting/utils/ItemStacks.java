package net.stevenrafferty.headhunting.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemStacks {

    public ItemStack emptyItemStack() {
        ItemStack empty = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
        ItemMeta emptyMeta = empty.getItemMeta();
        emptyMeta.setDisplayName(" ");
        emptyMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        empty.setItemMeta(emptyMeta);
        return empty;
    }

    public ItemStack skullItemStack(String type, Creature killed) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwner(type);
        skullMeta.setDisplayName(ChatColor.GREEN + killed.getName() + "'s Head");
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public ItemStack tokenItemStack(String type) {
        ItemStack token = new ItemStack(Material.NETHER_STAR, 1);
        ItemMeta tokenMeta = token.getItemMeta();
        tokenMeta.setDisplayName(ChatColor.GREEN + type + " Token");
        tokenMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        token.setItemMeta(tokenMeta);
        return token;
    }


}
