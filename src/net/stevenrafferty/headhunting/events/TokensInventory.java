package net.stevenrafferty.headhunting.events;

import net.stevenrafferty.headhunting.Main;
import net.stevenrafferty.headhunting.utils.Helper;
import net.stevenrafferty.headhunting.utils.ItemStacks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class TokensInventory implements Listener {

    private Plugin plugin = Main.getPlugin(Main.class);

    private ItemStacks itemStacks = new ItemStacks();

    private Helper helper = new Helper();

    String tokenInventoryName = helper.getConfigMessage("options.token_inventory_name");

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        ClickType click = event.getClick();
        Inventory tokenInventory = event.getClickedInventory();
        Inventory playerInventory = player.getInventory();
        ItemStack item = event.getCurrentItem();

        if (tokenInventory == null) {
            return;
        }
        if (tokenInventory.getName().equals(tokenInventoryName)) {
            event.setCancelled(true);
            if (item == null || !item.hasItemMeta() || item.getType().equals(Material.AIR)) {
                return;
            }
            ItemMeta itemMeta = item.getItemMeta();

            String creature = helper.convertToVisibleString(itemMeta.getLore().get(0));
            ItemStack skull = itemStacks.skullItemStack(creature);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            ItemStack[] contents = playerInventory.getContents();

            int headsRequire = plugin.getConfig().getInt("creatures." + creature + ".token.heads.required");

            // Loop through contents of player inventory
            // Check if the player has any mob heads and exchange for token
            int skullAmount = 0;
            for (int i = 0; i < contents.length; i++) {
                ItemStack content = contents[i];
                if (content != null && content.hasItemMeta() && content.getType().equals(Material.SKULL_ITEM)) {
                    SkullMeta contentMeta = (SkullMeta) content.getItemMeta();
                    if (contentMeta.getDisplayName().equals(skullMeta.getDisplayName()) && contentMeta.getOwner().equals(skullMeta.getOwner())) {
                        skullAmount += content.getAmount();
                    }
                }
                // Break out of loop, as no need to count any more
                if (skullAmount == headsRequire) {
                    break;
                }
            }
            String giveTokenMessage = helper.getConfigMessage("messages.give_token_message");
            String notEnoughHeads = helper.getConfigMessage("messages.not_enough_heads");

            if (skullAmount >= headsRequire) {
                playerInventory.remove(skull);
                helper.removeHeads(playerInventory, skull, headsRequire);
                player.sendMessage(giveTokenMessage);
                playerInventory.addItem(itemStacks.tokenItemStack(creature));
            } else {
                player.sendMessage(notEnoughHeads);
            }
        }
    }


}
