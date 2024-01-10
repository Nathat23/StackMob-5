package uk.antiperson.stackmob.listeners;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;

public class RemoveListener implements Listener {

    private final StackMob sm;
    public RemoveListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler
    public void onEntityRemove(EntityRemoveFromWorldEvent event) {
        if (!(event.getEntity() instanceof Mob)) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity) event.getEntity();
        StackEntity stackEntity = sm.getEntityManager().getStackEntity(livingEntity);
        if (stackEntity == null) {
            return;
        }
        if (stackEntity.isRemoved()) {
            return;
        }
        sm.getEntityManager().unregisterStackedEntity(stackEntity);
    }

    @EventHandler
    public void onEntityAdd(EntityAddToWorldEvent event) {
        if (!(event.getEntity() instanceof Mob)) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity) event.getEntity();
        if (!sm.getEntityManager().hasStackData(livingEntity)) {
            return;
        }
        sm.getEntityManager().registerStackedEntity(livingEntity);
    }
}
