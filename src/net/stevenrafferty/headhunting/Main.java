package net.stevenrafferty.headhunting;

import net.stevenrafferty.headhunting.commands.*;
import net.stevenrafferty.headhunting.events.*;
import net.stevenrafferty.headhunting.utils.Database;
import net.stevenrafferty.headhunting.utils.Helper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import net.milkbowl.vault.economy.Economy;

import java.sql.SQLException;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

  private static final Logger log = Logger.getLogger("Minecraft");
  public static Economy econ = null;

  @Override
  public void onEnable() {
    // Listeners
    getServer().getPluginManager().registerEvents(new MobHeadDrop(), this);
    getServer().getPluginManager().registerEvents(new TokensInventory(), this);
    getServer().getPluginManager().registerEvents(new PlayerKill(), this);
    getServer().getPluginManager().registerEvents(new MaskInventory(), this);
    getServer().getPluginManager().registerEvents(new DisableHeadPlace(), this);
    getServer().getPluginManager().registerEvents(new MaskEffects(), this);
    getServer().getPluginManager().registerEvents(new UpgradeInventory(), this);

    // Commands
    getCommand("redeem").setExecutor(new Redeem());
    getCommand("souls").setExecutor(new Souls());
    getCommand("headhunting").setExecutor(new Help());
    getCommand("mask").setExecutor(new Mask());
    getCommand("upgrade").setExecutor(new Upgrade());

    getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "HeadHunting: Enabled");
    loadConfig();
    removeMaterials();

    // Economy
    if(!setupEconomy()){
      Bukkit.shutdown();
    }

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

  @Override
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

  private boolean setupEconomy() {
    RegisteredServiceProvider<Economy> economyProviter = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
    if(economyProviter != null) {
      econ = economyProviter.getProvider();
    }
    return (econ != null);
  }

  public static Economy getEconomy() {
    return econ;
  }

}
