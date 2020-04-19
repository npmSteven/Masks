package net.stevenrafferty.headhunting.commands;

import net.stevenrafferty.headhunting.Main;
import net.stevenrafferty.headhunting.utils.Helper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Help implements CommandExecutor {

    Helper helper = new Helper();

    String noConsoleCommandMessage = helper.getConfigMessage("messages.no_console_command_message");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(helper.getConfigMessage("messages.help"));
        } else {
            sender.sendMessage(noConsoleCommandMessage);
        }
        return true;
    }

}
