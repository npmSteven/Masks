package net.stevenrafferty.headhunting.events;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R1.ItemSkull;
import net.stevenrafferty.headhunting.utils.MobHeads;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class MobHeadDrop implements Listener {

  MobHeads mobHeads = new MobHeads();

  // Check if entity is dead and if so we will drop a head
  @EventHandler
  public void onEntityDeath(EntityDeathEvent event) {
    if (event.getEntity() instanceof Creature) {
      Creature killed = (Creature) event.getEntity();
      if (killed.getKiller() instanceof Player) {
        Player player = killed.getKiller();
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
