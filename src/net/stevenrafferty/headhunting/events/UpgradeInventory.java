package net.stevenrafferty.headhunting.events;

import net.milkbowl.vault.economy.Economy;
import net.stevenrafferty.headhunting.Main;
import net.stevenrafferty.headhunting.utils.Helper;
import net.stevenrafferty.headhunting.utils.ItemStacks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class UpgradeInventory implements Listener {

    Plugin plugin = Main.getPlugin(Main.class);

    Helper helper = new Helper();

    ItemStacks itemStacks = new ItemStacks();

    String upgradeInventoryName = helper.getConfigMessage("options.upgrade_inventory_name");

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Inventory clickedInventory = event.getClickedInventory();
        ItemStack item = event.getCurrentItem();
        if (player.getOpenInventory().getTitle().equals(upgradeInventoryName)) {
            event.setCancelled(true);
        }
        if (clickedInventory == null || item == null || !item.hasItemMeta() || item.getType().equals(Material.AIR)) {
            return;
        }

        // Check if player clicked there inventory
        if (clickedInventory.getType() == InventoryType.PLAYER && player.getOpenInventory().getTitle().equals(upgradeInventoryName)) {
            ItemStack mask = player.getOpenInventory().getItem(4);
            // Check if player has click a mask
            if (hasHelmet(item) && mask.getType() == Material.AIR) {
                // add mask to menu
                player.getOpenInventory().setItem(4, item);
                clickedInventory.removeItem(item);

                ItemMeta itemMeta = item.getItemMeta();
                String[] creatureLore = helper.getItemMetaInfo(itemMeta);
                String creature = creatureLore[0];
                String tier = creatureLore[1];

                updateRequirements(tier, creature, event);
            }
        } else if (clickedInventory.getTitle().equals(upgradeInventoryName)) {
            // Check if player has click a mas
            ItemStack mask = player.getOpenInventory().getItem(4);
            if (hasHelmet(mask)) {
                clickedInventory.removeItem(mask);
                player.getInventory().addItem(mask);

                String upgradeText = helper.getConfigMessage("options.upgrade_inventory");

                List<String> lore = new ArrayList<>();
                ItemStack upgrade = findLoopedItemStack(event.getInventory().getContents(), upgradeText);
                ItemMeta upgradeMeta = upgrade.getItemMeta();
                upgradeMeta.setLore(lore);
                upgrade.setItemMeta(upgradeMeta);
            }
        }


        if (clickedInventory.getTitle().equals(upgradeInventoryName)) {
            String closeInventory = helper.getConfigMessage("options.close_inventory");
            String upgradeInventory = helper.getConfigMessage("options.upgrade_inventory");

            // Upgrade
            ItemStack foundItemUpgrade = findItemStack(item, upgradeInventory);

            if (foundItemUpgrade != null) {
                // Check if helmet is there
                ItemStack helmet = clickedInventory.getItem(4);
                if (hasHelmet(helmet)) {
                    ItemMeta helmetMeta = helmet.getItemMeta();
                    String[] creatureLore = helper.getItemMetaInfo(helmetMeta);
                    String creature = creatureLore[0];
                    String tier = creatureLore[1];

                    int nextTier = Integer.parseInt(tier);
                    nextTier = nextTier + 1;

                    String tierPath = "creatures." + creature + ".masks." + nextTier;
                    String tierName = helper.getConfigMessage(tierPath + ".name");
                    int amountRequired = plugin.getConfig().getInt(tierPath + ".money.required");
                    int tokenRequired = plugin.getConfig().getInt(tierPath + ".token.required");

                    if (tierName != null) {

                        ItemStack token = itemStacks.tokenItemStack(creature, false);

                        if (checkHasEnoughMoney(player, amountRequired)) {
                            if (checkHasEnoughTokens(tokenRequired, player, token)) {
                                // Remove token
                                helper.removeTokens(player.getInventory(), token, tokenRequired);

                                // Remove money
                                Economy economy = Main.getEconomy();
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
                                economy.withdrawPlayer(offlinePlayer, amountRequired);

                                // Update mask with new tier
                                List<String> lore = new ArrayList<>();
                                lore.add(0, Helper.convertToInvisibleString(creature + "-" + nextTier));
                                lore.add(1, tierName);
                                helmetMeta.setLore(lore);
                                helmet.setItemMeta(helmetMeta);

                                // Play sound
                                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 0);

                                // Send message to player
                                String upgradedMask = helper.getConfigMessage("messages.upgraded_mask");
                                player.sendMessage(upgradedMask);

                                // Update requirements
                                updateRequirements(Integer.toString(nextTier), creature, event);
                            }
                        }



                        // only run when we have enough tokens and money

                    } else {
                        String maskMaxTier = helper.getConfigMessage("messages.mask_max_tier");
                        player.sendMessage(maskMaxTier);
                    }
                } else {
                    String addMaskToSlot = helper.getConfigMessage("messages.add_mask_to_slot");
                    player.sendMessage(addMaskToSlot);
                }
            }

            // Close
            ItemStack foundItemClose = findItemStack(item, closeInventory);

            if (foundItemClose != null && player.getOpenInventory().getTitle().equals(upgradeInventoryName)) {
                ItemStack helmet = clickedInventory.getItem(4);
                if (hasHelmet(helmet)) {
                    clickedInventory.removeItem(helmet);
                    player.getInventory().addItem(helmet);
                }
                player.getOpenInventory().close();
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory closedInventory = event.getInventory();
        Player player = (Player) event.getPlayer();
        if (closedInventory.getTitle().equals(upgradeInventoryName)) {
            ItemStack helmet = closedInventory.getItem(4);
            if (hasHelmet(helmet)) {
                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.openInventory(closedInventory);
                    }
                }, 1L);
            }
        }
    }

    public boolean hasHelmet(ItemStack helmet) {
        boolean hasHelmet = false;
        if (helmet != null) {
            if (helmet.hasItemMeta()) {
                ItemMeta helmetMeta = helmet.getItemMeta();
                if (helmetMeta.hasDisplayName() && helmetMeta.hasLore()) {
                    String[] creatureLore = helper.getItemMetaInfo(helmetMeta);
                    if (creatureLore.length > 1) {
                        String creature = creatureLore[0];
                        String tier = creatureLore[1];
                        if (creature != null && tier != null) {
                            hasHelmet = true;
                        }
                    }
                }
            }
        }
        return hasHelmet;
    }

    public ItemStack findItemStack(ItemStack item, String name) {
        ItemStack foundItem = null;
        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta.hasDisplayName()) {
                if (itemMeta.getDisplayName().equals(name)) {
                    foundItem = item;
                }
            }
        }
        return foundItem;
    }

    public ItemStack findLoopedItemStack(ItemStack[] contents, String name) {
        ItemStack foundItem = null;
        for (ItemStack content : contents) {
            if (content != null) {
                if (content.hasItemMeta()) {
                    ItemMeta contentMeta = content.getItemMeta();
                    if (contentMeta.hasDisplayName()) {
                        if (contentMeta.getDisplayName().equals(name)) {
                            foundItem = content;
                        }
                    }
                }
            }
        }
        return foundItem;
    }

    public void updateRequirements(String tier, String creature, InventoryClickEvent event) {
        int nextTier = Integer.parseInt(tier);
        nextTier = nextTier + 1;

        String tierPath = "creatures." + creature + ".masks." + nextTier;
        String tierName = helper.getConfigMessage(tierPath + ".name");

        if (tierName != null) {
            int tokensRequired = plugin.getConfig().getInt("creatures." + creature + ".masks." + nextTier + ".token.required");
            String tokenName = helper.getConfigMessage("creatures." + creature + ".masks." + nextTier + ".token.name");

            int moneyRequired = plugin.getConfig().getInt("creatures." + creature + ".masks." + nextTier + ".money.required");
            String moneyName = helper.getConfigMessage("creatures." + creature + ".masks." + nextTier + ".money.name");

            List<String> lore = new ArrayList<>();
            lore.add(tokenName + tokensRequired);
            lore.add(moneyName + moneyRequired);

            String upgradeText = helper.getConfigMessage("options.upgrade_inventory");

            ItemStack upgrade = findLoopedItemStack(event.getInventory().getContents(), upgradeText);
            ItemMeta upgradeMeta = upgrade.getItemMeta();
            upgradeMeta.setLore(lore);
            upgrade.setItemMeta(upgradeMeta);
        } else {
            String upgradeText = helper.getConfigMessage("options.upgrade_inventory");

            List<String> lore = new ArrayList<>();
            lore.add(helper.getConfigMessage("options.max_tier"));
            ItemStack upgrade = findLoopedItemStack(event.getInventory().getContents(), upgradeText);
            ItemMeta upgradeMeta = upgrade.getItemMeta();
            upgradeMeta.setLore(lore);
            upgrade.setItemMeta(upgradeMeta);
        }
    }

    public boolean checkHasEnoughMoney(Player player, int requiredAmount) {

        Economy economy = Main.getEconomy();

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());

        double balance = economy.getBalance(offlinePlayer);

        if (balance >= requiredAmount) {
            return true;
        }
        player.sendMessage("not enough money");
        return false;
    }

    public boolean checkHasEnoughTokens(int tokensRequired, Player player, ItemStack token) {
        Inventory playerInventory = player.getInventory();
        ItemStack[] contents = playerInventory.getContents();

        ItemMeta tokenMeta = token.getItemMeta();

        int tokenAmount = 0;
        for (int i = 0; i < contents.length; i ++) {
            ItemStack content = contents[i];
            if (content != null && content.hasItemMeta() && content.getType().equals(Material.NETHER_STAR)) {
                ItemMeta contentMeta = content.getItemMeta();
                if (contentMeta.hasDisplayName()) {
                    if (contentMeta.getDisplayName().equals(tokenMeta.getDisplayName()) && contentMeta.hasLore()) {
                        tokenAmount += content.getAmount();
                    }
                }
            }
            if (tokenAmount == tokensRequired) {
                break;
            }
        }
        String notEnoughTokens = helper.getConfigMessage("messages.not_enough_tokens");

        if (tokenAmount >= tokensRequired) {
            return true;
        } else {
            player.sendMessage(notEnoughTokens);
            return false;
        }
    }

}
