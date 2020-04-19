package net.stevenrafferty.headhunting.events;

import net.stevenrafferty.headhunting.Main;
import net.stevenrafferty.headhunting.handlers.Experience;
import net.stevenrafferty.headhunting.utils.Database;
import net.stevenrafferty.headhunting.utils.Helper;
import net.stevenrafferty.headhunting.utils.ItemStacks;
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
    private Experience experience = new Experience();
    private Database database = new Database();

    String tokenInventoryName = helper.getConfigMessage("options.token_inventory_name");

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        ClickType click = event.getClick();
        Inventory clickedInventory = event.getClickedInventory();
        ItemStack item = event.getCurrentItem();
        if (clickedInventory == null) {
            return;
        }
        if (player.getOpenInventory().getTitle().equals(tokenInventoryName)) {
            event.setCancelled(true);
        }

        if (clickedInventory.getName().equals(tokenInventoryName)) {
            event.setCancelled(true);
            if (item == null || !item.hasItemMeta() || item.getType().equals(Material.AIR)) {
                return;
            }
            Inventory playerInventory = player.getInventory();
            ItemMeta itemMeta = item.getItemMeta();
            String creature = helper.convertToVisibleString(itemMeta.getLore().get(0));
            ItemStack skull = itemStacks.skullItemStack(creature);
            String giveTokenMessage = helper.getConfigMessage("messages.give_token_message");
            int headsRequired = plugin.getConfig().getInt("creatures." + creature + ".token.heads.required");
            int soulsRequired = plugin.getConfig().getInt("creatures." + creature + ".token.souls.required");

            boolean hasEnoughHeads = checkPlayerHasEnoughHeads(item, player);

            if (hasEnoughHeads) {
                boolean hasEnoughXp = checkPlayerHasEnoughXp(player, creature);
                if (hasEnoughXp) {
                    boolean hasEnoughPlayerSouls = checkPlayerHasEnoughSouls(player, soulsRequired);
                    if (hasEnoughPlayerSouls) {
                        // Remove heads
                        helper.removeHeads(playerInventory, skull, headsRequired);

                        // Remove xp
                        int xpRequired = plugin.getConfig().getInt("creatures." + creature + ".token.xp.required");
                        int xpRequiredLevel = experience.getExpAtLevel(xpRequired);
                        experience.changePlayerExp(player, xpRequiredLevel);

                        // Remove Souls
                        int souls = database.getSouls(player);
                        int newSouls = souls - soulsRequired;
                        database.updateSouls(player, newSouls);

                        player.sendMessage(giveTokenMessage);
                        playerInventory.addItem(itemStacks.tokenItemStack(creature, false));
                    }
                }
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

    public boolean checkPlayerHasEnoughSouls(Player player, int soulsRequired) {
        String notEnoughSouls = helper.getConfigMessage("messages.not_enough_souls");
        String noSouls = helper.getConfigMessage("messages.no_souls");
        boolean hasEnoughSouls = false;
        if (database.hasPlayer(player)) {
            int souls = database.getSouls(player);
            if (souls >= soulsRequired) {
                hasEnoughSouls = true;
            } else {
                player.sendMessage(notEnoughSouls);
            }
        } else {
            player.sendMessage(noSouls);
        }
        return hasEnoughSouls;
    }


}
