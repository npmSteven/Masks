package net.stevenrafferty.headhunting.events;

import net.milkbowl.vault.economy.Economy;
import net.stevenrafferty.headhunting.Main;
import net.stevenrafferty.headhunting.utils.Helper;
import net.stevenrafferty.headhunting.utils.ItemStacks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class MaskInventory implements Listener {

    private Plugin plugin = Main.getPlugin(Main.class);

    private ItemStacks itemStacks = new ItemStacks();
    private Helper helper = new Helper();

    String maskInventoryName = helper.getConfigMessage("options.mask_inventory_name");

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Inventory clickedInventory = event.getClickedInventory();
        ItemStack item = event.getCurrentItem();
        if (clickedInventory == null) {
            return;
        }
        if (player.getOpenInventory().getTitle().equals(maskInventoryName)) {
            event.setCancelled(true);
        }
        if (clickedInventory.getName().equals(maskInventoryName)) {
            if (item == null || !item.hasItemMeta() || item.getType().equals(Material.AIR)) {
                return;
            }
            Inventory playerInventory = player.getInventory();
            ItemMeta itemMeta = item.getItemMeta();

            String[] creatureLore = helper.getItemMetaInfo(itemMeta);
            String creature = creatureLore[0];
            String tier = creatureLore[1];

            ItemStack token = itemStacks.tokenItemStack(creature, false);
            ItemStack mask = itemStacks.maskItemStack(creature, tier, false);

            int tokensRequired = plugin.getConfig().getInt("creatures." + creature + ".masks." + tier + ".token.required");
            int moneyRequired = plugin.getConfig().getInt("creatures." + creature + ".masks." + tier + ".money.required");

            // check if inventory contains creature tokens
            // get the creature tokens required amount
            // check if the inventory contains enough tokens to satisfy the required amount
            boolean hasEnoughTokens = checkHasEnoughTokens(tokensRequired, player, token);
            boolean hasEnoughMoney = checkHasEnoughMoney(player, moneyRequired);

            // if all of the above checks pass then remove the token from the players inventory
            // Send them the mask
            if (hasEnoughTokens && hasEnoughMoney) {
                String giveMaskMessage = helper.getConfigMessage("messages.give_mask_message");

                // Remove token
                helper.removeTokens(playerInventory, token, tokensRequired);

                // Remove money
                Economy economy = Main.getEconomy();
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
                economy.withdrawPlayer(offlinePlayer, moneyRequired);

                // Give mask
                playerInventory.addItem(mask);
                player.sendMessage(giveMaskMessage);
            }
        }
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

}
