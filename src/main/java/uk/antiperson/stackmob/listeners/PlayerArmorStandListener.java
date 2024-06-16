package uk.antiperson.stackmob.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import uk.antiperson.stackmob.StackMob;

@ListenerMetadata(config = "display-name.nearby.armorstand.enabled")
public class PlayerArmorStandListener implements Listener {

    private final StackMob sm;
    public PlayerArmorStandListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        sm.getPlayerManager().stopWatching(event.getPlayer());
    }
}
