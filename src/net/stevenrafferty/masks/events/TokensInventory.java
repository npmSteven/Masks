package net.stevenrafferty.masks.events;

import de.tr7zw.nbtapi.NBTItem;
import net.stevenrafferty.masks.Main;
import net.stevenrafferty.masks.handlers.Experience;
import net.stevenrafferty.masks.utils.Database;
import net.stevenrafferty.masks.utils.Helper;
import net.stevenrafferty.masks.utils.ItemStacks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

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

        Inventory clickedInventory = event.getClickedInventory();
        ItemStack item = event.getCurrentItem();

        if (clickedInventory == null) return;

        if (player.getOpenInventory().getTitle().equals(tokenInventoryName)) {
            event.setCancelled(true);
        }

        if (event.getView().getTitle().equals(tokenInventoryName)) {
            if (item == null || !item.hasItemMeta() || item.getType().equals(Material.AIR)) {
                return;
            }
            Inventory playerInventory = player.getInventory();

            // Get item data
            NBTItem nbti = new NBTItem(item);
            String creature = nbti.getString("creature");

            ItemStack skull = itemStacks.skullItemStack(creature);

            String giveTokenMessage = helper.getConfigMessage("messages.give_token_message");

            // Check config for the souls and heads required to obtain a token
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
                        if (soulsRequired > 0) {
                            int souls = database.getSouls(player);
                            int newSouls = souls - soulsRequired;
                            database.updateSouls(player, newSouls);
                        }

                        player.sendMessage(giveTokenMessage);
                        playerInventory.addItem(itemStacks.tokenItemStack(creature, false));
                    }
                }
            }
        }
    }

    public boolean checkPlayerHasEnoughHeads(ItemStack item, Player player) {
        Inventory playerInventory = player.getInventory();

        // Get item data
        NBTItem nbti = new NBTItem(item);
        String creature = nbti.getString("creature");

        ItemStack skull = itemStacks.skullItemStack(creature);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        ItemStack[] contents = playerInventory.getContents();

        int headsRequire = plugin.getConfig().getInt("creatures." + creature + ".token.heads.required");

        // Loop through contents of player inventory
        // Check if the player has any mob heads and exchange for token
        int skullAmount = 0;
        for (int i = 0; i < contents.length; i++) {
            ItemStack content = contents[i];
            if (content != null && content.hasItemMeta() && content.getType().equals(Material.PLAYER_HEAD)) {
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
        if (soulsRequired == 0) {
            return true;
        }

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
