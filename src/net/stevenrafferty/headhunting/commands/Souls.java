package net.stevenrafferty.headhunting.commands;

import net.stevenrafferty.headhunting.Main;
import net.stevenrafferty.headhunting.utils.Database;
import net.stevenrafferty.headhunting.utils.Helper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Souls implements CommandExecutor {

    private Plugin plugin = Main.getPlugin(Main.class);
    private Database database = new Database();
    private Helper helper = new Helper();

    String noConsoleCommandMessage = helper.getConfigMessage("messages.no_console_command_message");
    String noPermissionsMessage = helper.getConfigMessage("messages.no_permissions_message");
    String soulsCollectedMessage = helper.getConfigMessage("messages.souls_collected");

    String soulsPermission = plugin.getConfig().getString("permissions.souls");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission(soulsPermission)) {
                player.sendMessage(soulsCollectedMessage + database.getSouls(player));
            } else {
                player.sendMessage(noPermissionsMessage);
            }
        } else {
            sender.sendMessage(noConsoleCommandMessage);
        }
        return true;
    }

}
