package net.stevenrafferty.headhunting.events;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import de.tr7zw.nbtapi.NBTItem;
import net.stevenrafferty.headhunting.Main;
import net.stevenrafferty.headhunting.utils.Helper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Set;

public class MaskEffects implements Listener {

    Plugin plugin = Main.getPlugin(Main.class);

    Helper helper = new Helper();

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        ItemStack currentItem = event.getNewArmorPiece();
        ItemStack previousItem = event.getOldArmorPiece();
        Player player = event.getPlayer();

        // Remove potion effect when the player removes there mask
        if (previousItem != null) {
            if (previousItem.getType() == Material.DIAMOND_HELMET && previousItem.hasItemMeta()) {
                ItemMeta previousItemMeta = previousItem.getItemMeta();
                if (previousItemMeta.hasLore()) {

                    // Get item data
                    NBTItem nbti = new NBTItem(previousItem);
                    String creature = nbti.getString("creature");
                    String tier = nbti.getString("tier");

                    // Check if item data exists
                    if (creature != null && tier != null) {

                        // Get all of the available effects from config.yml
                        String effectsPath = "creatures." + creature + ".masks." + tier + ".effects";
                        Set<String> effects = plugin.getConfig().getConfigurationSection(effectsPath).getKeys(false);

                        // Loop through all of the available effects and apply them to the player
                        for (String number : effects) {

                            String effect = plugin.getConfig().getString(effectsPath + "." + number + ".effect");
                            PotionEffectType type = PotionEffectType.getByName(effect.toUpperCase());
                            if (type == null) {
                                continue;
                            }

                            // Remove potion effects that was applied from previous mask
                            if (player.hasPotionEffect(type)) {
                                player.removePotionEffect(type);
                            }
                        }
                    }
                }
            }
        }

        // Apply effects when the player puts a mask on
        if (currentItem != null) {
            // Apply potion effects to do with mask
            if (currentItem.getType() == Material.DIAMOND_HELMET && currentItem.hasItemMeta()) {
                ItemMeta itemMeta = currentItem.getItemMeta();
                if (itemMeta.hasLore()) {

                    // Get item data
                    NBTItem nbti = new NBTItem(currentItem);
                    String creature = nbti.getString("creature");
                    String tier = nbti.getString("tier");

                    // Check if item data exists
                    if (creature != null && tier != null) {

                        // Get all of the available effects from config.yml
                        String effectsPath = "creatures." + creature + ".masks." + tier + ".effects";
                        Set<String> effects = plugin.getConfig().getConfigurationSection(effectsPath).getKeys(false);

                        // Loop through all of the available effects and apply them to the player
                        for (String number : effects) {

                            // Get the effect to apply and the amplifier
                            String effect = plugin.getConfig().getString(effectsPath + "." + number + ".effect");
                            int amplifier = plugin.getConfig().getInt(effectsPath + "." + number + ".amplifier");

                            PotionEffectType type = PotionEffectType.getByName(effect.toUpperCase());

                            if (type == null) {
                                continue;
                            }

                            // Ensure we remove the effect just in-case the user already has this effect applied
                            if (player.hasPotionEffect(type)) {
                                player.removePotionEffect(type);
                            }

                            // Apply effect to player
                            PotionEffect buildEffect = type.createEffect(Integer.MAX_VALUE, amplifier);
                            player.addPotionEffect(buildEffect);
                        }
                    }
                }
            }
        }
    }

}
