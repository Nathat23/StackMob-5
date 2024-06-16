package uk.antiperson.stackmob.tasks;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.utils.Utilities;

import java.util.HashSet;
import java.util.Set;

public class MergeTask implements Runnable {

    private final StackMob sm;

    public MergeTask(StackMob sm) {
        this.sm = sm;
    }

    private void checkEntity(StackEntity original, boolean checkHasMoved, double checkHasMovedDistance) {
        if (original.isWaiting()) {
            original.incrementWait();
            return;
        }
        if (!original.canStack()) {
            if (!original.getEntity().isValid()) {
                removeEntity(original);
            }
            return;
        }
        if (checkHasMoved) {
            if (original.getEntity().getWorld().equals(original.getLastLocation().getWorld())) {
                if (!original.skipLastLocation()) {
                    if (original.getEntity().getLocation().distance(original.getLastLocation()) < checkHasMovedDistance) {
                        return;
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
                    removeEntity(removed);
                    if (original == removed) {
                        return;
                    }
                    break;
                }
                continue;
            }
            matches.add(nearbyStack);
        }
        if (!stackThresholdEnabled) {
            return;
        }
        int threshold = original.getEntityConfig().getStackThreshold() - 1;
        int size = matches.size();
        if (size < threshold) {
            return;
        }
        for (StackEntity match : matches) {
            match.remove(false);
            removeEntity(match);
        }
        if (size + original.getSize() > original.getMaxSize()) {
            final int toCompleteStack = (original.getMaxSize() - original.getSize());
            original.incrementSize(toCompleteStack);
            for (int stackSize : Utilities.split(size - toCompleteStack, original.getMaxSize())) {
                StackEntity stackEntity = original.duplicate();
                stackEntity.setSize(stackSize);
            }
            return;
        }
        original.incrementSize(size);
    }

    private void removeEntity(StackEntity stackEntity) {
        sm.getEntityManager().unregisterStackedEntity(stackEntity);
    }

    @Override
    public void run() {
        boolean checkHasMoved = sm.getMainConfig().getConfig().isCheckHasMoved();
        double checkHasMovedDistance = sm.getMainConfig().getConfig().getCheckHasMovedDistance();
        for (StackEntity original : sm.getEntityManager().getStackEntities()) {
            Runnable runnable = () -> checkEntity(original, checkHasMoved, checkHasMovedDistance);
            if (Utilities.IS_FOLIA) {
                sm.getScheduler().runTask(sm, original.getEntity(), runnable);
            } else {
                runnable.run();
            }
        }
    }
}
