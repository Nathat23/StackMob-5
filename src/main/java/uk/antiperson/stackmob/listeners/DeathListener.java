package uk.antiperson.stackmob.listeners;

import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.Drops;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.entity.death.DeathMethod;
import uk.antiperson.stackmob.entity.death.DeathType;
import uk.antiperson.stackmob.events.EventHelper;
import uk.antiperson.stackmob.events.StackDeathEvent;
import uk.antiperson.stackmob.utils.Utilities;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class DeathListener implements Listener {

    private final StackMob sm;
    public DeathListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler
    public void onStackDeath(EntityDeathEvent event) {
        if (!sm.getEntityManager().isStackedEntity(event.getEntity())) {
            return;
        }
        StackEntity stackEntity = sm.getEntityManager().getStackEntity(event.getEntity());
        DeathMethod deathMethod = calculateDeath(stackEntity);
        int deathStep = Math.min(stackEntity.getSize(), deathMethod.calculateStep());
        StackDeathEvent stackDeathEvent = EventHelper.callStackDeathEvent(stackEntity, deathStep);
        deathStep = stackDeathEvent.getDeathStep();
        int toMultiply = deathStep - 1;
        if (sm.getMainConfig().getConfigFile().getBoolean("traits.leashed")) {
            if (event.getEntity().isLeashed() && (stackEntity.getSize() - deathStep) != 0) {
                event.getEntity().setMetadata(Utilities.NO_LEASH_METADATA, new FixedMetadataValue(sm, true));
            }
        }
        if (deathStep < stackEntity.getSize()) {
            if (stackEntity.getEntityConfig().isSkipDeathAnimation()) {
                toMultiply = deathStep;
                event.setCancelled(true);
                stackEntity.incrementSize(-deathStep);
                deathMethod.onSpawn(stackEntity);
            } else {
                stackEntity.removeStackData();
                int finalDeathStep = deathStep;
                sm.getScheduler().runTask(sm, event.getEntity(), () -> {
                    StackEntity spawned = stackEntity.duplicate();
                    spawned.setSize(stackEntity.getSize() - finalDeathStep);
                    deathMethod.onSpawn(spawned);
                });
            }
        }
        if (toMultiply == 0) {
            return;
        }
        int experience = stackEntity.getDrops().calculateDeathExperience(toMultiply, event.getDroppedExp());
        Map<ItemStack, Integer> drops = stackEntity.getDrops().calculateDrops(toMultiply, event.getDrops());
        Drops.dropItems(event.getEntity().getLocation(), drops);
        event.setDroppedExp(experience);
        if (Utilities.isPaper() && event.isCancelled()) {
            ExperienceOrb orb = (ExperienceOrb) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.EXPERIENCE_ORB);
            orb.setExperience(experience);
        }
        if (stackEntity.getEntityConfig().isPlayerStatMulti()) {
            if (event.getEntity().getKiller() != null) {
                event.getEntity().getKiller().incrementStatistic(Statistic.KILL_ENTITY, event.getEntityType(), toMultiply);
            }
        }
        if (event.getEntity() instanceof Slime && stackEntity.getEntityConfig().isSlimeMultiEnabled()) {
            event.getEntity().setMetadata("deathcount", new FixedMetadataValue(sm, toMultiply));
        }
    }

    public DeathMethod calculateDeath(StackEntity entity) {
        DeathType deathType = entity.getEntityConfig().getDeathType(entity.getEntity());
        try {
            return deathType.getStepClass().getDeclaredConstructor(StackMob.class, StackEntity.class).newInstance(sm, entity);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("Error while determining death step!");
        }
    }


}
