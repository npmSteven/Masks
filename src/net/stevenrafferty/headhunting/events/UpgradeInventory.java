package net.stevenrafferty.headhunting.events;

import de.tr7zw.nbtapi.NBTItem;
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

            // Check if player has clicked a mask
            if (isMask(item) && mask == null) {

                // add mask to menu
                player.getOpenInventory().setItem(4, item);
                clickedInventory.removeItem(item);

                // Get item data
                NBTItem nbti = new NBTItem(item);
                String creature = nbti.getString("creature");
                String tier = nbti.getString("tier");

                updateRequirements(tier, creature, event);
            }
        } else if (event.getView().getTitle().equals(upgradeInventoryName)) {
            ItemStack mask = clickedInventory.getItem(4);
            // Check if player has click a mask
            if (mask != null) {
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


        if (event.getView().getTitle().equals(upgradeInventoryName)) {
            String closeInventory = helper.getConfigMessage("options.close_inventory");
            String upgradeInventory = helper.getConfigMessage("options.upgrade_inventory");

            // Upgrade
            ItemStack foundItemUpgrade = findItemStack(item, upgradeInventory);

            if (foundItemUpgrade != null) {
                // Check if helmet is there
                ItemStack helmet = clickedInventory.getItem(4);
                if (isMask(helmet)) {
                    ItemMeta helmetMeta = helmet.getItemMeta();

                    // Get item data
                    NBTItem nbti = new NBTItem(helmet);
                    String creature = nbti.getString("creature");
                    String tier = nbti.getString("tier");

                    // Update to next tier for info
                    int nextTier = Integer.parseInt(tier);
                    nextTier = nextTier + 1;

                    String tierPath = "creatures." + creature + ".masks." + nextTier;
                    String tierName = helper.getConfigMessage(tierPath + ".name");

                    // Get the money and tokens required
                    int moneyRequired = plugin.getConfig().getInt(tierPath + ".money.required");
                    int tokensRequired = plugin.getConfig().getInt(tierPath + ".token.required");

                    // Check if the tier exists
                    if (tierName != null) {

                        ItemStack token = itemStacks.tokenItemStack(creature, false);

                        if (checkHasEnoughMoney(player, moneyRequired)) {
                            if (checkHasEnoughTokens(tokensRequired, player, token)) {
                                // Remove token
                                helper.removeTokens(player.getInventory(), token, tokensRequired);

                                // Remove money
                                Economy economy = Main.getEconomy();
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
                                economy.withdrawPlayer(offlinePlayer, moneyRequired);

                                // Update mask with new tier
                                List<String> lore = new ArrayList<>();
                                lore.add(0, tierName);
                                helmetMeta.setLore(lore);
                                helmet.setItemMeta(helmetMeta);

                                // Play sound
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 0);

                                // Send message to player
                                String upgradedMask = helper.getConfigMessage("messages.upgraded_mask");
                                player.sendMessage(upgradedMask);

                                // Update requirements
                                updateRequirements(Integer.toString(nextTier), creature, event);
                            }
                        }

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
                if (isMask(helmet)) {
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
        if (event.getView().getTitle().equals(upgradeInventoryName)) {
            ItemStack helmet = closedInventory.getItem(4);
            if (isMask(helmet)) {
                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.openInventory(closedInventory);
                    }
                }, 1L);
            }
        }
    }

    public boolean isMask(ItemStack helmet) {
        boolean isMask = false;
        if (helmet != null) {
            if (helmet.hasItemMeta()) {
                ItemMeta helmetMeta = helmet.getItemMeta();
                if (helmetMeta.hasDisplayName() && helmetMeta.hasLore()) {
                    // Get item data
                    NBTItem nbti = new NBTItem(helmet);
                    String creature = nbti.getString("creature");
                    String tier = nbti.getString("tier");
                    if (creature != null && tier != null) {
                        isMask = true;
                    }
                }
            }
        }
        return isMask;
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
