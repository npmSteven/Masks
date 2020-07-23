package net.stevenrafferty.headhunting.utils;

import de.tr7zw.nbtapi.NBTItem;
import net.stevenrafferty.headhunting.Main;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
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
        String name = helper.getConfigMessage("creatures." + creature + ".head.name");

        String type = plugin.getConfig().getString("creatures." + creature + ".head.type");
        int amount = plugin.getConfig().getInt("creatures." + creature + ".head.drop_amount");

        ItemStack skull = new ItemStack(Material.LEGACY_SKULL_ITEM, amount, (short) 3);
        NBTItem nbti = new NBTItem(skull);
        nbti.setString("creature", creature);
        SkullMeta skullMeta = (SkullMeta) nbti.getItem().getItemMeta();
        skullMeta.setOwner(type);
        skullMeta.setDisplayName(name);
        skullMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        nbti.getItem().setItemMeta(skullMeta);
        return nbti.getItem();
    }

    public ItemStack maskItemStack(String creature, String tier, boolean hasLore) {
        String name = helper.getConfigMessage("creatures." + creature + ".masks.name");
        String loreTier = helper.getConfigMessage("creatures." + creature + ".masks." + tier + ".name");

        int tokensRequired = plugin.getConfig().getInt("creatures." + creature + ".masks." + tier + ".token.required");
        String tokenName = helper.getConfigMessage("creatures." + creature + ".masks." + tier + ".token.name");

        int moneyRequired = plugin.getConfig().getInt("creatures." + creature + ".masks." + tier + ".money.required");
        String moneyName = helper.getConfigMessage("creatures." + creature + ".masks." + tier + ".money.name");

        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET, 1);
        NBTItem nbti = new NBTItem(helmet);
        nbti.setString("creature", creature);
        nbti.setString("tier", tier);
        ItemMeta helmetMeta = nbti.getItem().getItemMeta();
        helmetMeta.setDisplayName(name);

        List<String> lore = new ArrayList<>();
        lore.add(loreTier);
        if (hasLore) {
            if (tokensRequired > 0) {
                lore.add(tokenName + tokensRequired);
            }
            if (moneyRequired > 0) {
                lore.add(moneyName + moneyRequired);
            }
        }
        helmetMeta.setLore(lore);
        helmetMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        nbti.getItem().setItemMeta(helmetMeta);
        return nbti.getItem();
    }

    public ItemStack tokenItemStack(String creature, boolean hasLore) {
        String tokenPath = "creatures." + creature + ".token.";

        String name = helper.getConfigMessage(tokenPath + "name");

        String headsText = helper.getConfigMessage(tokenPath + "heads.text");
        int headsRequired = plugin.getConfig().getInt(tokenPath + "heads.required");

        String xpText = helper.getConfigMessage(tokenPath + "xp.text");
        int xpRequired = plugin.getConfig().getInt(tokenPath + "xp.required");

        String soulsText = helper.getConfigMessage(tokenPath + "souls.text");
        int soulsRequired = plugin.getConfig().getInt(tokenPath + "souls.required");

        ItemStack token = new ItemStack(Material.NETHER_STAR, 1);
        NBTItem nbti = new NBTItem(token);
        nbti.setString("creature", creature);
        ItemMeta tokenMeta = nbti.getItem().getItemMeta();
        tokenMeta.setDisplayName(name);

        List<String> lore = new ArrayList<>();
        if (hasLore) {
            if (headsRequired > 0) {
                lore.add(headsText + headsRequired);
            }
            if (xpRequired > 0) {
                lore.add(xpText + xpRequired);
            }
            if (soulsRequired > 0) {
                lore.add(soulsText + soulsRequired);
            }
        }
        tokenMeta.setLore(lore);

        tokenMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        nbti.getItem().setItemMeta(tokenMeta);

        return nbti.getItem();
    }

    public ItemStack upgradeItemStack() {
        String upgradeText = helper.getConfigMessage("options.upgrade_inventory");

        ItemStack upgrade = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
        ItemMeta upgradeMeta = upgrade.getItemMeta();
        upgradeMeta.setDisplayName(upgradeText);
        upgradeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        upgrade.setItemMeta(upgradeMeta);
        return upgrade;
    }

    public ItemStack emptyItemStack() {
        ItemStack empty = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta emptyMeta = empty.getItemMeta();
        emptyMeta.setDisplayName(" ");
        emptyMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        empty.setItemMeta(emptyMeta);
        return empty;
    }

    public ItemStack closeItemStack() {
        String close = helper.getConfigMessage("options.close_inventory");

        ItemStack closeItem = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        ItemMeta closeItemMeta = closeItem.getItemMeta();
        closeItemMeta.setDisplayName(close);
        closeItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        closeItem.setItemMeta(closeItemMeta);
        return closeItem;
    }

}
