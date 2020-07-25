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

import java.util.Set;

public class Mask implements CommandExecutor {


    private Plugin plugin = Main.getPlugin(Main.class);

    Helper helper = new Helper();

    ItemStacks itemStacks = new ItemStacks();

    String masksPermission = plugin.getConfig().getString("permissions.masks");
    String noConsoleCommandMessage = helper.getConfigMessage("messages.no_console_command_message");
    String noPermissionsMessage = helper.getConfigMessage("messages.no_permissions_message");

    String maskInventoryName = helper.getConfigMessage("options.mask_inventory_name");

    Set creatures = plugin.getConfig().getConfigurationSection("creatures.").getKeys(false);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission(masksPermission)) {
                Inventory inventory = plugin.getServer().createInventory(null, 18, maskInventoryName);

                int index = 0;
                for (Object key : creatures) {
                    String creature = key.toString();

                    ItemStack token = itemStacks.maskItemStack(creature, 1, true);
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
