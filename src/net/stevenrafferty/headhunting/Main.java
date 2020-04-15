package net.stevenrafferty.headhunting;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

  public void onEnable() {
    getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "HeadHunting has been Enabled");
  }

  public void onDisable() {
    getServer().getConsoleSender().sendMessage(ChatColor.RED + "HeadHunting has been Disabled");
  }

}
