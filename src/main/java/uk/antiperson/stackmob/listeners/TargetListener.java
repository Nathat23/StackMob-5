package uk.antiperson.stackmob.listeners;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.utils.Utilities;

@ListenerMetadata(config = "disable-targeting.enabled")
public class TargetListener implements Listener {

    private final StackMob sm;
    public TargetListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler
    public void onMobTarget(EntityTargetEvent event) {
        if (!(event.getEntity() instanceof Monster)) {
            return;
        }
        if (!sm.getEntityManager().isStackedEntity((LivingEntity) event.getEntity())){
            return;
        }
        if (sm.getMainConfig().getTargetingDisabledTypes(event.getEntityType()).contains(event.getEntityType().toString())) {
            return;
        }
        if (Utilities.isPaper() && sm.getMainConfig().getTargetingDisabledReasons(event.getEntityType()).contains(event.getEntity().getEntitySpawnReason())){
            return;
        }
        event.setCancelled(true);
    }
}
