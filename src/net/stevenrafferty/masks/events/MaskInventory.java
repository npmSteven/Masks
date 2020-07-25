package net.stevenrafferty.masks.events;

import de.tr7zw.nbtapi.NBTItem;
import net.milkbowl.vault.economy.Economy;
import net.stevenrafferty.masks.Main;
import net.stevenrafferty.masks.utils.Helper;
import net.stevenrafferty.masks.utils.ItemStacks;
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

        if (clickedInventory == null) return;

        if (player.getOpenInventory().getTitle().equals(maskInventoryName)) {
            event.setCancelled(true);
        }

        // Check if the event was clicked inside the mask inventory
        if (event.getView().getTitle().equals(maskInventoryName)) {

            // Check if the click item exists
            if (item == null || !item.hasItemMeta() || item.getType().equals(Material.AIR)) {
                return;
            }

            Inventory playerInventory = player.getInventory();

            NBTItem nbti = new NBTItem(item);
            String creature = nbti.getString("creature");
            int tier = nbti.getInteger("tier");

            ItemStack token = itemStacks.tokenItemStack(creature, false);
            ItemStack mask = itemStacks.maskItemStack(creature, tier, false);

            int tokensRequired = plugin.getConfig().getInt("creatures." + creature + ".masks." + tier + ".token.required");
            int moneyRequired = plugin.getConfig().getInt("creatures." + creature + ".masks." + tier + ".money.required");

            if (checkHasEnoughTokens(tokensRequired, player, token)) {
                if (checkHasEnoughMoney(player, moneyRequired)) {
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
    }

    public boolean checkHasEnoughTokens(int tokensRequired, Player player, ItemStack token) {
        Inventory playerInventory = player.getInventory();
        ItemStack[] contents = playerInventory.getContents();

        ItemMeta tokenMeta = token.getItemMeta();

        // Loop through the players contents and check how many tokens they have
        int tokenAmount = 0;
        for (int i = 0; i < contents.length; i ++) {
            ItemStack content = contents[i];
            if (content != null && content.hasItemMeta() && content.getType().equals(Material.NETHER_STAR)) {
                ItemMeta contentMeta = content.getItemMeta();
                if (contentMeta.hasDisplayName()) {
                    if (contentMeta.getDisplayName().equals(tokenMeta.getDisplayName())) {
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
        player.sendMessage(helper.getConfigMessage("messages.not_enough_money"));
        return false;
    }

}
