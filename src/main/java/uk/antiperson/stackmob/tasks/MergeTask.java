package uk.antiperson.stackmob.tasks;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.scheduler.BukkitRunnable;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.utils.Utilities;

import java.util.HashSet;
import java.util.Set;

public class MergeTask extends BukkitRunnable {

    private final StackMob sm;

    public MergeTask(StackMob sm) {
        this.sm = sm;
    }

    public void run() {
        HashSet<StackEntity> toRemove = new HashSet<>();
        for (StackEntity original : sm.getEntityManager().getStackEntities()) {
            if (original.isWaiting()) {
                original.incrementWait();
                continue;
            }
            if (!original.canStack()) {
                if (!original.getEntity().isValid()) {
                    toRemove.add(original);
                }
                continue;
            }
            Integer[] searchRadius = sm.getMainConfig().getStackRadius(original.getEntity().getType());
            Set<StackEntity> matches = new HashSet<>();
            for (Entity nearby : original.getEntity().getNearbyEntities(searchRadius[0], searchRadius[1], searchRadius[2])) {
                if (!(nearby instanceof Mob)) {
                    continue;
                }
                if (!sm.getEntityManager().isStackedEntity((LivingEntity) nearby)) {
                    continue;
                }
                StackEntity nearbyStack = sm.getEntityManager().getStackEntity((LivingEntity) nearby);
                if (nearbyStack == null) {
                    continue;
                }
                if (!nearbyStack.canStack()) {
                    continue;
                }
                if (!original.match(nearbyStack)) {
                    continue;
                }
                if (nearbyStack.getSize() > 1 || original.getSize() > 1) {
                    StackEntity removed = original.merge(nearbyStack, false);
                    if (removed != null) {
                        toRemove.add(removed);
                        break;
                    }
                    continue;
                }
                matches.add(nearbyStack);
            }
            if (!sm.getMainConfig().getStackThresholdEnabled(original.getEntity().getType())) {
                continue;
            }
            int threshold = sm.getMainConfig().getStackThreshold(original.getEntity().getType()) - 1;
            int size = matches.size();
            if (size < threshold) {
                continue;
            }
            for (StackEntity match : matches) {
                match.remove(false);
                toRemove.add(match);
            }
            if (size + original.getSize() > original.getMaxSize()) {
                for (int stackSize : Utilities.split(size, original.getMaxSize())) {
                    StackEntity stackEntity = original.duplicate();
                    stackEntity.setSize(stackSize);
                }
                return;
            }
            original.incrementSize(size);
        }
        toRemove.forEach(stackEntity -> sm.getEntityManager().unregisterStackedEntity(stackEntity));
    }

}
