package net.stevenrafferty.headhunting.commands;

import net.stevenrafferty.headhunting.Main;
import net.stevenrafferty.headhunting.utils.ItemStacks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class Redeem implements CommandExecutor {

    private Plugin plugin = Main.getPlugin(Main.class);

    private ItemStacks itemStacks = new ItemStacks();

    String redeemPermission = plugin.getConfig().getString("permissions.redeem");
    String noPermissionsMessage = plugin.getConfig().getString("messages.no_permissions_message");
    String noConsoleCommandMessage = plugin.getConfig().getString("messages.no_console_command_message");
    String tokenInventoryName = plugin.getConfig().getString("options.token_inventory_name");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission(redeemPermission)) {
                Inventory inventory = plugin.getServer().createInventory(null, 9, tokenInventoryName);

                ItemStack empty = itemStacks.emptyItemStack();

                inventory.setItem(0, itemStacks.tokenItemStack("Cow"));
                inventory.setItem(1, itemStacks.tokenItemStack("Pig"));
                inventory.setItem(2, itemStacks.tokenItemStack("Iron Golem"));
                inventory.setItem(3, itemStacks.tokenItemStack("Skeleton"));
                inventory.setItem(4, itemStacks.tokenItemStack("Zombie"));
                inventory.setItem(5, itemStacks.tokenItemStack("Blaze"));
//                inventory.setItem(6, empty);
//                inventory.setItem(7, empty);
//                inventory.setItem(8, empty);

                player.openInventory(inventory);
            } else {
                player.sendMessage(noPermissionsMessage);
            }
        } else {
            sender.sendMessage(noConsoleCommandMessage);
        }
        return true;
    }
}
