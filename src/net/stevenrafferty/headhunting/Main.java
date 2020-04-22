package net.stevenrafferty.headhunting;

import net.stevenrafferty.headhunting.commands.Help;
import net.stevenrafferty.headhunting.commands.Mask;
import net.stevenrafferty.headhunting.commands.Redeem;
import net.stevenrafferty.headhunting.commands.Souls;
import net.stevenrafferty.headhunting.events.*;
import net.stevenrafferty.headhunting.utils.Database;
import net.stevenrafferty.headhunting.utils.Helper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class Main extends JavaPlugin {

  public void onEnable() {
    // Listeners
    getServer().getPluginManager().registerEvents(new MobHeadDrop(), this);
    getServer().getPluginManager().registerEvents(new TokensInventory(), this);
    getServer().getPluginManager().registerEvents(new PlayerKill(), this);
    getServer().getPluginManager().registerEvents(new MaskInventory(), this);
    getServer().getPluginManager().registerEvents(new DisableHeadPlace(), this);
    getServer().getPluginManager().registerEvents(new MaskEffects(), this);

    // Commands
    getCommand("redeem").setExecutor(new Redeem());
    getCommand("souls").setExecutor(new Souls());
    getCommand("headhunting").setExecutor(new Help());
    getCommand("mask").setExecutor(new Mask());

    getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "HeadHunting: Enabled");
    loadConfig();
    removeMaterials();

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

  public void removeMaterials() {
    Helper helper = new Helper();
    helper.remove(Material.BEACON);
  }

}
