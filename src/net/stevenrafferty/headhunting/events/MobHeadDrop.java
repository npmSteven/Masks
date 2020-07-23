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
    // Check if the dead entity is an instance of Creature
    if (event.getEntity() instanceof Creature) {

      Creature killed = (Creature) event.getEntity();

      // Check if the killer is an instanceof player
      if (killed.getKiller() instanceof Player) {
        Player player = killed.getKiller();

        // Check if the user has the permission to use this event
        if (player.hasPermission(mob_head_drops)) {

          // Check if we should drop the mob head
          if (shouldDropMobHead(killed.getType())) {

            // Validate that the entity is dead
            if ((killed.isDead() || killed.getHealth() <= 0) && mobHeads.isValid(killed.getName())) {
              String creature = killed.getType().toString().toLowerCase();
              ItemStack skull = itemStacks.skullItemStack(creature);
              event.getDrops().add(skull);
            }
          }
        }
      }
    }
  }

  public boolean shouldDropMobHead(EntityType type) {
    String creature = type.toString().toLowerCase();
    int probability = plugin.getConfig().getInt("creatures." + creature + ".head.drop_probability");
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
