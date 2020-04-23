package net.stevenrafferty.headhunting.commands;

import net.stevenrafferty.headhunting.Main;
import net.stevenrafferty.headhunting.utils.Helper;
import net.stevenrafferty.headhunting.utils.ItemStacks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public class Upgrade implements CommandExecutor {

    private Plugin plugin = Main.getPlugin(Main.class);

    private Helper helper = new Helper();

    private ItemStacks itemStacks = new ItemStacks();

    String noConsoleCommandMessage = helper.getConfigMessage("messages.no_console_command_message");
    String noPermissionsMessage = helper.getConfigMessage("messages.no_permissions_message");
    String upgradeInventoryName = helper.getConfigMessage("options.upgrade_inventory_name");

    String upgradePermission = plugin.getConfig().getString("permissions.upgrade");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission(upgradePermission)) {
                Inventory inventory = plugin.getServer().createInventory(null, 9, upgradeInventoryName);

                inventory.setItem(0, itemStacks.emptyItemStack());
                inventory.setItem(1, itemStacks.emptyItemStack());
                inventory.setItem(2, itemStacks.emptyItemStack());
                inventory.setItem(3, itemStacks.closeItemStack());
                // Mask
                inventory.setItem(5, itemStacks.upgradeItemStack());
                inventory.setItem(6, itemStacks.emptyItemStack());
                inventory.setItem(7, itemStacks.emptyItemStack());
                inventory.setItem(8, itemStacks.emptyItemStack());

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
