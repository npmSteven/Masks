package net.stevenrafferty.headhunting.events;

import net.stevenrafferty.headhunting.Main;
import net.stevenrafferty.headhunting.utils.ItemStacks;
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
  String mob_head_drops = plugin.getConfig().getString("permissions.mob_head_drops");

  MobHeads mobHeads = new MobHeads();

  ItemStacks itemStacks = new ItemStacks();

  // Check if entity is dead and if so we will drop a head
  @EventHandler
  public void onEntityDeath(EntityDeathEvent event) {
    if (event.getEntity() instanceof Creature) {
      Creature killed = (Creature) event.getEntity();
      if (killed.getKiller() instanceof Player) {
        Player player = killed.getKiller();
        if (player.hasPermission(mob_head_drops)) {
          if (shouldDropMobHead(killed.getType())) {
            if ((killed.isDead() || killed.getHealth() <= 0) && mobHeads.isType(killed.getType())) {
              String type = mobHeads.getOwnerOfType(killed.getType());
              ItemStack skull = itemStacks.skullItemStack(type, killed);
              event.getDrops().add(skull);
            }
          }
        }
      }
    }
  }

  public boolean shouldDropMobHead(EntityType type) {
    int probability = plugin.getConfig().getInt("creatures." + type.toString().toLowerCase() + ".head_drop_probability");
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
