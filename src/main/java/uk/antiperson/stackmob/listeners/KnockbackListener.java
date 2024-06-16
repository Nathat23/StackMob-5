package uk.antiperson.stackmob.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityKnockbackEvent;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;

@ListenerMetadata(config = "disable-knockback.enabled")
public class KnockbackListener implements Listener {

    private final StackMob sm;
    public KnockbackListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler
    public void onEntityKnockback(EntityKnockbackEvent event) {
        StackEntity stackEntity = sm.getEntityManager().getStackEntity(event.getEntity());
        if (stackEntity == null) {
            return;
        }
        if (!stackEntity.getEntityConfig().isKnockbackDisabledTypes()) {
            return;
        }
        if (!stackEntity.getEntityConfig().isKnockbackDisabledReasons(event.getEntity().getEntitySpawnReason())) {
            return;
        }
        if (!stackEntity.getEntityConfig().isKnockbackDisabledCause(event.getCause())) {
            return;
        }
        event.setCancelled(true);
    }

}
