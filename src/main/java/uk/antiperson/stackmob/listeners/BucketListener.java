package uk.antiperson.stackmob.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;

@ListenerMetadata(config = "events.divide.bucket-fill")
public class BucketListener implements Listener {

    private final StackMob sm;

    public BucketListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBucketFill(PlayerBucketEntityEvent event) {
        final StackEntity stackEntity = sm.getEntityManager().getStackEntity((LivingEntity) event.getEntity());
        if (stackEntity == null) {
            return;
        }
        if (stackEntity.isSingle()) {
            return;
        }
        stackEntity.slice();
        stackEntity.remove();
        event.getEntityBucket().setItemMeta(null);
    }


}
