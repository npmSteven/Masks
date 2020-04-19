package net.stevenrafferty.headhunting.utils;

import net.stevenrafferty.headhunting.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.UUID;

public class Database {

  Plugin plugin = Main.getPlugin(Main.class);

  private static Connection connection;
  public String host, database, username, password, table;
  public int port;

  public void connect() throws ClassNotFoundException, SQLException {
    synchronized (this) {
      if (getConnection() != null && !getConnection().isClosed()) {
        return;
      }
      setConnectionDetails();
      Class.forName("com.mysql.jdbc.Driver");

      System.out.print("host: " + host);
      System.out.print("port: " + port);
      System.out.print("database: " + database);
      System.out.print("username: " + username);
      System.out.print("password: " + password);

      setConnection(DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password));
      Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "HeadHunting: MySQL Connected!");
    }
  }

  public void setConnectionDetails() {
    String path = "database.";

    host = plugin.getConfig().getString(path + "host");
    port = plugin.getConfig().getInt(path + "port");
    database = plugin.getConfig().getString(path + "database");
    username = plugin.getConfig().getString(path + "username");
    password = plugin.getConfig().getString(path + "password");
    table = plugin.getConfig().getString(path + "table");
  }

  public boolean hasPlayer(Player player) {
    try {
      boolean hasPlayer = false;
      PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM playerkills WHERE Player_UUID = ?");
      statement.setString(1, player.getUniqueId().toString());
      ResultSet result = statement.executeQuery();
      if (result.next()) {
        hasPlayer = true;
      }
      return hasPlayer;
    } catch (SQLException error) {
      error.printStackTrace();
      return false;
    }
  }

  public void createKills(Player player, int kills, int souls) {
    try {
      PreparedStatement statement = getConnection().prepareStatement("INSERT INTO playerkills(Player_UUID, Kills, Souls) VALUES (?, ?, ?)");
      statement.setString(1, player.getUniqueId().toString());
      statement.setInt(2, kills);
      statement.setInt(3, souls);
      statement.executeUpdate();
    } catch (SQLException error) {
      error.printStackTrace();
    }
  }

  public void updateKills(Player player, int kills) {
    try {
      int playerKills = getKills(player);
      int totalPlayerKills = playerKills + kills;

      PreparedStatement statement = getConnection().prepareStatement("UPDATE playerkills SET Kills = ? WHERE Player_UUID = ?");
      statement.setInt(1, totalPlayerKills);
      statement.setString(2, player.getUniqueId().toString());
      statement.executeUpdate();
    } catch (SQLException error) {
      error.printStackTrace();
    }
  }

  public int getKills(Player player) {
    try {
      int kills = 0;
      PreparedStatement statement = getConnection().prepareStatement("SELECT Kills FROM playerkills WHERE Player_UUID = ?");
      statement.setString(1, player.getUniqueId().toString());
      ResultSet result = statement.executeQuery();
      while (result.next()) {
        kills = result.getInt("Kills");
      }
      return kills;
    } catch (SQLException error) {
      error.printStackTrace();
      return 0;
    }
  }

  public void updateSouls(Player player, int souls) {
    try {
      int playerSouls = getSouls(player);
      int totalPlayerSouls = playerSouls + souls;

      PreparedStatement statement = getConnection().prepareStatement("UPDATE playerkills SET Souls = ? WHERE Player_UUID = ?");
      statement.setInt(1, totalPlayerSouls);
      statement.setString(2, player.getUniqueId().toString());
      statement.executeUpdate();
    } catch (SQLException error) {
      error.printStackTrace();
    }
  }

  public int getSouls(Player player) {
    try {
      int souls = 0;
      PreparedStatement statement = getConnection().prepareStatement("SELECT Souls FROM playerkills WHERE Player_UUID = ?");
      statement.setString(1, player.getUniqueId().toString());
      ResultSet result = statement.executeQuery();
      while (result.next()) {
        souls = result.getInt("Souls");
      }
      return souls;
    } catch (SQLException error) {
      error.printStackTrace();
      return 0;
    }
  }

  public void setConnection(Connection connection) {
    this.connection = connection;
  }

  public Connection getConnection() {
    return connection;
  }

}
