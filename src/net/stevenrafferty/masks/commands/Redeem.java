package net.stevenrafferty.masks.commands;

import net.stevenrafferty.masks.Main;
import net.stevenrafferty.masks.utils.Helper;
import net.stevenrafferty.masks.utils.ItemStacks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class Redeem implements CommandExecutor {

    private Plugin plugin = Main.getPlugin(Main.class);

    private ItemStacks itemStacks = new ItemStacks();

    private Helper helper = new Helper();

    String redeemPermission = plugin.getConfig().getString("permissions.redeem");

    String noPermissionsMessage = helper.getConfigMessage("messages.no_permissions_message");
    String noConsoleCommandMessage = helper.getConfigMessage("messages.no_console_command_message");
    String tokenInventoryName = helper.getConfigMessage("options.token_inventory_name");

    Set creatures = plugin.getConfig().getConfigurationSection("creatures.").getKeys(false);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission(redeemPermission)) {
                Inventory inventory = plugin.getServer().createInventory(null, 18, tokenInventoryName);

                int index = 0;
                for (Object key : creatures) {
                    String creature = key.toString();

                    ItemStack token = itemStacks.tokenItemStack(creature, true);
                    inventory.setItem(index, token);
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
