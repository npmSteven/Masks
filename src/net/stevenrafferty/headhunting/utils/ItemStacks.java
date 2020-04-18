package net.stevenrafferty.headhunting.utils;

import net.stevenrafferty.headhunting.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

    private Helper helper = new Helper();

    public ItemStack skullItemStack(String creature) {
        String name = plugin.getConfig().getString("creatures." + creature + ".head.name");
        String type = plugin.getConfig().getString("creatures." + creature + ".head.type");
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 50, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwner(type);
        skullMeta.setDisplayName(name);

        List<String> lore = new ArrayList<>();
        lore.add(helper.convertToInvisibleString(creature));
        skullMeta.setLore(lore);
        skullMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public ItemStack tokenItemStack(String creature) {
        String tokenPath = "creatures." + creature + ".token.";

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
        lore.add(helper.convertToInvisibleString(creature));
        lore.add(headsText + headsRequired);
        lore.add(xpText + xpRequired);
        lore.add(killsText + killsRequired);
        tokenMeta.setLore(lore);

        tokenMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        token.setItemMeta(tokenMeta);

        return token;
    }

}