package net.stevenrafferty.headhunting.events;

import net.stevenrafferty.headhunting.utils.Database;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.sql.SQLException;

public class PlayerKill implements Listener {

    private Database database = new Database();

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player != null) {
            System.out.print(player.getName());
            boolean hasPlayer = database.hasPlayer(player);
            if (hasPlayer) {
                database.updateKills(player, 1);
                database.updateSouls(player, 1);
            } else {
                database.createKills(player, 1, 1);
            }
        }
    }

}
