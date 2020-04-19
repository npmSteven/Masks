package net.stevenrafferty.headhunting.events;

import net.stevenrafferty.headhunting.utils.Database;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerKill implements Listener {

    private Database database = new Database();

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player != null) {
            System.out.print(player.getName());
            boolean hasPlayer = database.hasPlayer(player);
            if (hasPlayer) {
                updateKills(player);
                updateSouls(player);
            } else {
                database.createKills(player, 1, 1);
            }
        }
    }

    public void updateKills(Player player) {
        int currentKills = database.getKills(player);
        int newCurrentKills = currentKills + 1;
        database.updateKills(player, newCurrentKills);
    }

    public void updateSouls(Player player) {
        int currentSouls = database.getSouls(player);
        int newCurrentSouls = currentSouls + 1;
        database.updateSouls(player, newCurrentSouls);

    }

}
