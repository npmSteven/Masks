package net.stevenrafferty.masks;

import net.milkbowl.vault.economy.Economy;
import net.stevenrafferty.masks.commands.*;
import net.stevenrafferty.masks.events.*;
import net.stevenrafferty.masks.utils.Database;
import net.stevenrafferty.masks.utils.Helper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class Main extends JavaPlugin {

  private static Economy econ = null;

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
    getCommand("masks").setExecutor(new Mask());
    getCommand("upgrade").setExecutor(new Upgrade());

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

    if (!setupEconomy()) {
      System.out.println("No economy plugin found. Disabling Vault");
      getServer().getPluginManager().disablePlugin(this);
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
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      return false;
    }
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
      return false;
    }
    econ = rsp.getProvider();
    return econ != null;
  }

  public static Economy getEconomy() {
    return econ;
  }

}
