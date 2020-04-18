package net.stevenrafferty.headhunting.utils;

import net.stevenrafferty.headhunting.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Creature;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ItemStacks {

    private Plugin plugin = Main.getPlugin(Main.class);

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

    public ItemStack tokenItemStack(String tokenPath) {
        String name = plugin.getConfig().getString(tokenPath + "name");

        String headsText = plugin.getConfig().getString(tokenPath + "heads.text");
        int headsRequired = plugin.getConfig().getInt(tokenPath + "heads.required");

        String xpText = plugin.getConfig().getString(tokenPath + "xp.text");
        int xpRequired = plugin.getConfig().getInt(tokenPath + "xp.required");

        String killsText = plugin.getConfig().getString(tokenPath + "player_kills.text");
        int killsRequired = plugin.getConfig().getInt(tokenPath + "player_kills.required");

        ItemStack token = new ItemStack(Material.NETHER_STAR, 1);
        ItemMeta tokenMeta = token.getItemMeta();
        tokenMeta.setDisplayName(name);
        List<String> lore = new ArrayList<>();
        lore.add(headsText + headsRequired);
        lore.add(xpText + xpRequired);
        lore.add(killsText + killsRequired);
        tokenMeta.setLore(lore);

        tokenMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        token.setItemMeta(tokenMeta);
        return token;
    }


}
