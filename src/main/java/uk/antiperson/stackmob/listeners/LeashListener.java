package uk.antiperson.stackmob.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.utils.Utilities;

@ListenerMetadata(config = "traits.leashed")
public class LeashListener implements Listener {

    private final StackMob sm;
    public LeashListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler
    public void onLeash(EntityDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getType() != Material.LEAD) {
            return;
        }
        if (!event.getEntity().hasMetadata(Utilities.NO_LEASH_METADATA)) {
            return;
        }
        event.getEntity().removeMetadata(Utilities.NO_LEASH_METADATA, sm);
        event.setCancelled(true);
    }
}
