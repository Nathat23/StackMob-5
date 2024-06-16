package uk.antiperson.stackmob.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.config.EntityConfig;
import uk.antiperson.stackmob.entity.StackEntity;

import java.util.concurrent.ThreadLocalRandom;

@ListenerMetadata(config = "events.explosion.enabled")
public class ExplosionListener implements Listener {

    private final StackMob sm;

    public ExplosionListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        StackEntity stackEntity = sm.getEntityManager().getStackEntity((LivingEntity) event.getEntity());
        if (stackEntity == null || stackEntity.isSingle()) {
            return;
        }
        switch (stackEntity.getEntityConfig().getListenerMode(EntityConfig.EventType.EXPLOSION)) {
            case SPLIT:
                stackEntity.slice();
                break;
            case MULTIPLY:
                double multiplier = ThreadLocalRandom.current().nextDouble(0.4, 0.6);
                int toMultiply = stackEntity.getEntityConfig().getEventMultiplyLimit(EntityConfig.EventType.EXPLOSION, stackEntity.getSize());
                event.setYield(event.getYield() + Math.round(event.getYield() * toMultiply * multiplier));
                break;
        }
    }
}
