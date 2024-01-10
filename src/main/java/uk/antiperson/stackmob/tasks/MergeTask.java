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
        boolean checkHasMoved = sm.getMainConfig().getConfig().isCheckHasMoved();
        double checkHasMovedDistance = sm.getMainConfig().getConfig().getCheckHasMovedDistance();
        originals: for (StackEntity original : sm.getEntityManager().getStackEntities()) {
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
            if (checkHasMoved) {
                if (original.getEntity().getWorld().equals(original.getLastLocation().getWorld())) {
                    if (!original.skipLastLocation()) {
                        if (original.getEntity().getLocation().distance(original.getLastLocation()) < checkHasMovedDistance) {
                            continue;
                        }
                    }
                }
                original.setLastLocation(original.getEntity().getLocation());
            }
            boolean stackThresholdEnabled = original.getEntityConfig().getStackThresholdEnabled();
            Integer[] searchRadius = original.getEntityConfig().getStackRadius();
            Set<StackEntity> matches = new HashSet<>();
            for (Entity nearby : original.getEntity().getNearbyEntities(searchRadius[0], searchRadius[1], searchRadius[2])) {
                if (!(nearby instanceof Mob)) {
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
                if (!stackThresholdEnabled || (nearbyStack.getSize() > 1 || original.getSize() > 1)) {
                    final StackEntity removed = nearbyStack.merge(original, false);
                    if (removed != null) {
                        toRemove.add(removed);
                        if (original == removed) {
                            continue originals;
                        }
                        break;
                    }
                    continue;
                }
                matches.add(nearbyStack);
            }
            if (!stackThresholdEnabled) {
                continue;
            }
            int threshold = original.getEntityConfig().getStackThreshold() - 1;
            int size = matches.size();
            if (size < threshold) {
                continue;
            }
            for (StackEntity match : matches) {
                match.remove(false);
                toRemove.add(match);
            }
            if (size + original.getSize() > original.getMaxSize()) {
                final int toCompleteStack = (original.getMaxSize() - original.getSize());
                original.incrementSize(toCompleteStack);
                for (int stackSize : Utilities.split(size - toCompleteStack, original.getMaxSize())) {
                    StackEntity stackEntity = original.duplicate();
                    stackEntity.setSize(stackSize);
                }
                continue;
            }
            original.incrementSize(size);
        }
        toRemove.forEach(stackEntity -> sm.getEntityManager().unregisterStackedEntity(stackEntity));
    }

}
