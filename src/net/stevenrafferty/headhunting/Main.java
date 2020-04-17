package net.stevenrafferty.headhunting;

import net.stevenrafferty.headhunting.commands.Redeem;
import net.stevenrafferty.headhunting.events.MobHeadDrop;
import net.stevenrafferty.headhunting.events.TokensInventory;
import net.stevenrafferty.headhunting.utils.Database;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class Main extends JavaPlugin {

  public void onEnable() {
    // Listeners
    getServer().getPluginManager().registerEvents(new MobHeadDrop(), this);
    getServer().getPluginManager().registerEvents(new TokensInventory(), this);

    // Commands
    getCommand("redeem").setExecutor(new Redeem());

    getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "HeadHunting: Enabled");
    loadConfig();

    // Connect to db
    Database database = new Database();
    try {
      database.connect();
    } catch (ClassNotFoundException error) {
      error.printStackTrace();
    } catch (SQLException error) {
      error.printStackTrace();
    }
  }

  public void onDisable() {
    getServer().getConsoleSender().sendMessage(ChatColor.RED + "HeadHunting: Disabled");
  }

  public void loadConfig() {
    getConfig().options().copyDefaults(true);
    saveConfig();
  }

}
