package net.stevenrafferty.headhunting.events;

import net.stevenrafferty.headhunting.Main;
import net.stevenrafferty.headhunting.utils.MobHeads;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.Random;

public class MobHeadDrop implements Listener {

  Plugin plugin = Main.getPlugin(Main.class);

  MobHeads mobHeads = new MobHeads();

  // Check if entity is dead and if so we will drop a head
  @EventHandler
  public void onEntityDeath(EntityDeathEvent event) {
    if (event.getEntity() instanceof Creature) {
      Creature killed = (Creature) event.getEntity();
      if (killed.getKiller() instanceof Player) {
        Player player = killed.getKiller();
        if (player.hasPermission("headhunting.mob_head_drops")) {
          // Chance to drop
          if (shouldDropMobHead(killed.getType())) {
            if ((killed.isDead() || killed.getHealth() <= 0) && mobHeads.isType(killed.getType())) {
              String type = mobHeads.getOwnerOfType(killed.getType());
              ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
              SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
              skullMeta.setOwner(type);
              skullMeta.setDisplayName(ChatColor.GREEN + killed.getName() + "'s Head");
              skull.setItemMeta(skullMeta);
              event.getDrops().add(skull);
              player.sendMessage("You killed " + killed.getName());
            }
          }
        }
      }
    }
  }

  public boolean shouldDropMobHead(EntityType type) {
    int probability = plugin.getConfig().getInt("options.mob_head_drop_probability." + type.toString().toUpperCase());
    Random r = new Random();
    int low = 1;
    int high = 100;
    int randomNum = r.nextInt(high - low) + low;
    if (randomNum <= probability) {
      return true;
    }
    return false;
  }



}
