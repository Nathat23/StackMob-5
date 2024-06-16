package uk.antiperson.stackmob.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;
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
        StackEntity stackEntity = sm.getEntityManager().getStackEntity((LivingEntity) event.getEntity());
        if (stackEntity.getEntityConfig().isTargetingDisabledTypes()) {
            return;
        }
        if (Utilities.isPaper() && stackEntity.getEntityConfig().isTargetingDisabledReasons(event.getEntity().getEntitySpawnReason())){
            return;
        }
        event.setCancelled(true);
    }
}
