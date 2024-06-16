package uk.antiperson.stackmob.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;

@ListenerMetadata(config = "events.divide.tame")
public class TameListener implements Listener {

    private final StackMob sm;
    public TameListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler
    public void onTame(EntityTameEvent event) {
        StackEntity stackEntity = sm.getEntityManager().getStackEntity(event.getEntity());
        if (stackEntity == null) {
            return;
        }
        if (!stackEntity.isSingle()) {
            stackEntity.slice();
        }
        stackEntity.removeStackData();
    }
}
