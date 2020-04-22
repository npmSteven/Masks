package net.stevenrafferty.headhunting.events;

import com.codingforcookies.armorequip.ArmorEquipEvent;
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

        // Remove potion effect that was being applied from the previous mask
        if (previousItem != null) {
            if (previousItem.getType() == Material.DIAMOND_HELMET && previousItem.hasItemMeta()) {
                ItemMeta previousItemMeta = previousItem.getItemMeta();
                if (previousItemMeta.hasLore()) {
                    String[] creatureLore = helper.getItemMetaInfo(previousItemMeta);
                    String creature = creatureLore[0];
                    String tier = creatureLore[1];
                    if (creature != null && tier != null) {
                        String effectsPath = "creatures." + creature + ".masks." + tier + ".effects";
                        Set<String> effects = plugin.getConfig().getConfigurationSection(effectsPath).getKeys(false);
                        for (String number : effects) {
                            String effect = plugin.getConfig().getString(effectsPath + "." + number + ".effect");
                            PotionEffectType type = PotionEffectType.getByName(effect.toUpperCase());
                            if (type == null) {
                                continue;
                            }
                            if (player.hasPotionEffect(type)) {
                                player.removePotionEffect(type);
                            }
                        }
                    }
                }
            }
        }
        if (currentItem != null) {
            // Apply potion effects to do with mask
            if (currentItem.getType() == Material.DIAMOND_HELMET && currentItem.hasItemMeta()) {
                ItemMeta itemMeta = currentItem.getItemMeta();
                if (itemMeta.hasLore()) {
                    String[] creatureLore = helper.getItemMetaInfo(itemMeta);
                    String creature = creatureLore[0];
                    String tier = creatureLore[1];
                    if (creature != null && tier != null) {
                        String effectsPath = "creatures." + creature + ".masks." + tier + ".effects";
                        Set<String> effects = plugin.getConfig().getConfigurationSection(effectsPath).getKeys(false);
                        for (String number : effects) {
                            String effect = plugin.getConfig().getString(effectsPath + "." + number + ".effect");
                            int amplifier = plugin.getConfig().getInt(effectsPath + "." + number + ".amplifier");
                            PotionEffectType type = PotionEffectType.getByName(effect.toUpperCase());
                            if (type == null) {
                                continue;
                            }
                            if (player.hasPotionEffect(type)) {
                                player.removePotionEffect(type);
                            }
                            PotionEffect buildEffect = type.createEffect(Integer.MAX_VALUE, amplifier);
                            player.addPotionEffect(buildEffect);
                        }
                    }
                }
            }
        }
    }

}
