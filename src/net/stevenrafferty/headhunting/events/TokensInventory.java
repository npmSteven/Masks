package net.stevenrafferty.headhunting.events;

import net.stevenrafferty.headhunting.Main;
import net.stevenrafferty.headhunting.utils.ItemStacks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class TokensInventory implements Listener {

    private Plugin plugin = Main.getPlugin(Main.class);

    private ItemStacks itemStacks = new ItemStacks();

    String tokenInventoryName = plugin.getConfig().getString("options.token_inventory_name");

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
            if (item == null || !item.hasItemMeta()) {
                return;
            }
            player.sendMessage(item.getItemMeta().getDisplayName());
        }
    }

}
