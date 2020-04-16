package net.stevenrafferty.headhunting.utils;


import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import net.stevenrafferty.headhunting.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

  Plugin plugin = Main.getPlugin(Main.class);

  private Connection connection;
  public String host, database, username, password, table;
  public int port;

  public void connect() throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.jdbc.Driver");
    MysqlDataSource dataSource = new MysqlDataSource();

    setConnectionDetails();

    dataSource.setServerName(host);
    dataSource.setPort(port);
    dataSource.setUser(username);
    dataSource.setPassword(password);
    dataSource.setDatabaseName(database);

    connection = dataSource.getConnection();
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "HeadHunting: MySQL Connected!");
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


}
