package net.stevenrafferty.headhunting.events;

import net.stevenrafferty.headhunting.Main;
import net.stevenrafferty.headhunting.handlers.Experience;
import net.stevenrafferty.headhunting.utils.Helper;
import net.stevenrafferty.headhunting.utils.ItemStacks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
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

    private Experience experience = new Experience();

    String tokenInventoryName = helper.getConfigMessage("options.token_inventory_name");

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        ClickType click = event.getClick();
        Inventory tokenInventory = event.getClickedInventory();
        ItemStack item = event.getCurrentItem();

        if (tokenInventory == null) {
            return;
        }
        if (tokenInventory.getName().equals(tokenInventoryName)) {
            event.setCancelled(true);
            if (item == null || !item.hasItemMeta() || item.getType().equals(Material.AIR)) {
                return;
            }
            Inventory playerInventory = player.getInventory();
            ItemMeta itemMeta = item.getItemMeta();
            String creature = helper.convertToVisibleString(itemMeta.getLore().get(0));
            ItemStack skull = itemStacks.skullItemStack(creature);
            String giveTokenMessage = helper.getConfigMessage("messages.give_token_message");
            int headsRequire = plugin.getConfig().getInt("creatures." + creature + ".token.heads.required");

            boolean hasEnoughHeads = checkPlayerHasEnoughHeads(item, player);
            boolean hasEnoughXp = checkPlayerHasEnoughXp(player, creature);
//            boolean hasEnoughPlayerKills = checkPlayerHasEnoughKills(player);

            if (hasEnoughHeads && hasEnoughXp) {
                helper.removeHeads(playerInventory, skull, headsRequire);
                player.sendMessage(giveTokenMessage);
                playerInventory.addItem(itemStacks.tokenItemStack(creature));
            }
        }
    }

    public boolean checkPlayerHasEnoughHeads(ItemStack item, Player player) {
        Inventory playerInventory = player.getInventory();
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
                if (contentMeta.hasDisplayName()) {
                    if (contentMeta.getDisplayName().equals(skullMeta.getDisplayName()) && contentMeta.getOwner().equals(skullMeta.getOwner())) {
                        skullAmount += content.getAmount();
                    }
                }
            }
            // Break out of loop, as no need to count any more
            if (skullAmount == headsRequire) {
                break;
            }
        }
        String notEnoughHeads = helper.getConfigMessage("messages.not_enough_heads");

        if (skullAmount >= headsRequire) {

            return true;
        } else {
            player.sendMessage(notEnoughHeads);
            return false;
        }
    }

    public boolean checkPlayerHasEnoughXp(Player player, String creature) {
        int xpRequired = plugin.getConfig().getInt("creatures." + creature + ".token.xp.required");

        float xpRequiredLevel = experience.getExpAtLevel(xpRequired);
        float currentXp = experience.getPlayerExp(player);
        if (currentXp >= xpRequiredLevel) {
            return true;
        }
        String notEnoughXp = helper.getConfigMessage("messages.not_enough_xp");
        player.sendMessage(notEnoughXp);
        return false;
    }

    public boolean checkPlayerHasEnoughKills(Player player) {
        return false;
    }


}
