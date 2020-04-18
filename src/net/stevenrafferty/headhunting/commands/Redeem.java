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

import java.lang.reflect.Array;
import java.util.*;

public class Redeem implements CommandExecutor {

    private Plugin plugin = Main.getPlugin(Main.class);

    private ItemStacks itemStacks = new ItemStacks();

    String redeemPermission = plugin.getConfig().getString("permissions.redeem");
    String noPermissionsMessage = plugin.getConfig().getString("messages.no_permissions_message");
    String noConsoleCommandMessage = plugin.getConfig().getString("messages.no_console_command_message");
    String tokenInventoryName = plugin.getConfig().getString("options.token_inventory_name");
    Set creatures = plugin.getConfig().getConfigurationSection("creatures.").getKeys(false);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission(redeemPermission)) {
                Inventory inventory = plugin.getServer().createInventory(null, 9, tokenInventoryName);

                int index = 0;
                for (Object key : creatures) {
                    String tokenPath = "creatures." + key + ".token.";
                    inventory.setItem(index, itemStacks.tokenItemStack(tokenPath));
                    index++;
                }

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
