package uk.antiperson.stackmob.listeners;

import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.events.EventHelper;

public class SpawnListener implements Listener {

    private final StackMob sm;
    public SpawnListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Mob)) {
            return;
        }
        sm.getScheduler().runTask(sm, event.getEntity(), () -> {
            if (sm.getEntityManager().isStackedEntity(event.getEntity())) {
                StackEntity stackEntity = sm.getEntityManager().getStackEntity(event.getEntity());
                if (stackEntity != null && stackEntity.isForgetOnSpawn()) {
                    stackEntity.removeStackData();
                }
                return;
            }
            if (sm.getMainConfig().getConfig(event.getEntity().getType()).isEntityBlacklisted(event.getEntity(), event.getSpawnReason())) {
                return;
            }
            if (sm.getHookManager().spawnCheck(event.getEntity())) {
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
            sm.getHookManager().onSpawn(original);
            original.setSize(1);
            if (!original.getEntityConfig().isStackOnSpawn()) {
                return;
            }
            Integer[] searchRadius = original.getEntityConfig().getStackRadius();
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
                if (original.getEntityConfig().getStackThresholdEnabled() && nearby.getSize() == 1) {
                    continue;
                }
                StackEntity removed = nearby.merge(original, true);
                if (removed != null) {
                    removed.removeStackData();
                    return;
                }
            }
        });
    }
}
