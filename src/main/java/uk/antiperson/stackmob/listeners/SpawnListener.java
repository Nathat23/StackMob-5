package uk.antiperson.stackmob.listeners;

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.events.EventHelper;

public class SpawnListener implements Listener {

    private final StackMob sm;
    public SpawnListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler
    public void onSpawn(PreCreatureSpawnEvent event) {

    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Mob)) {
            return;
        }
        sm.getServer().getScheduler().runTask(sm, () -> {
            if (sm.getMainConfig().isEntityBlacklisted(event.getEntity(), event.getSpawnReason())) {
                return;
            }
            if (sm.getEntityManager().isStackedEntity(event.getEntity())) {
                StackEntity stackEntity = sm.getEntityManager().getStackEntity(event.getEntity());
                if (stackEntity != null && stackEntity.isForgetOnSpawn()) {
                    stackEntity.removeStackData();
                }
                return;
            }
            if (EventHelper.callStackSpawnEvent(event.getEntity()).isCancelled()) {
                return;
            }
            StackEntity original = sm.getEntityManager().registerStackedEntity(event.getEntity());
            if (original.shouldWait(event.getSpawnReason())) {
                original.makeWait();
                return;
            }
            Integer[] searchRadius = sm.getMainConfig().getStackRadius(event.getEntity().getType());
            for (Entity entity : event.getEntity().getNearbyEntities(searchRadius[0], searchRadius[1], searchRadius[2])) {
                if (!(entity instanceof Mob)) {
                    continue;
                }
                StackEntity nearby = sm.getEntityManager().getStackEntity((LivingEntity) entity);
                if (nearby == null) {
                    continue;
                }
                if (!nearby.canStack()) {
                    continue;
                }
                if (!original.match(nearby)) {
                    continue;
                }
                if (sm.getMainConfig().getStackThresholdEnabled(entity.getType()) && nearby.getSize() == 1) {
                    continue;
                }
                StackEntity removed = nearby.merge(original, true);
                if (removed != null) {
                    removed.removeStackData();
                    return;
                }
            }
            original.setSize(1);
            sm.getHookManager().onSpawn(original);
        });
    }
}
