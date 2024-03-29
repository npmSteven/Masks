package net.stevenrafferty.masks.utils;

import net.stevenrafferty.masks.Main;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class MobHeads {

  private Plugin plugin = Main.getPlugin(Main.class);

  public boolean isValid(String name) {
    Set creatures = plugin.getConfig().getConfigurationSection("creatures.").getKeys(false);
    boolean hasType = false;
    for (Object key : creatures) {
      String creature = key.toString().toLowerCase();
      name = name.replaceAll(" ", "_").toLowerCase();
      if (name.equals(creature)) {
        hasType = true;
        break;
      }
    }
    return hasType;
  }

}
