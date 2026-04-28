package com.parrot.lifesteal;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (ReviveStorage.hasPending(p.getUniqueId())) {

            HeartManager.set(p, 1);
            ReviveStorage.restore(p);
            ReviveStorage.remove(p.getUniqueId());
        }
    }
}
