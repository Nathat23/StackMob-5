package uk.antiperson.stackmob.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEnterBlockEvent;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;

@ListenerMetadata(config = "events.divide.enter-block")
public class BeeListener implements Listener {

    private final StackMob sm;

    public BeeListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler (ignoreCancelled = true)
    public void onEntityEnterBlockEvent(EntityEnterBlockEvent event) {
        LivingEntity bee = (LivingEntity) event.getEntity();
        if (!sm.getEntityManager().isStackedEntity(bee)) {
            return;
        }
        final StackEntity oldBee = sm.getEntityManager().getStackEntity(bee);
        if (oldBee == null || oldBee.isSingle()) {
            return;
        }
        oldBee.slice();
    }

}
