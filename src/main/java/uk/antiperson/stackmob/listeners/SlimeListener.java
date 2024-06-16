package uk.antiperson.stackmob.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SlimeSplitEvent;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.utils.Utilities;

import java.util.concurrent.ThreadLocalRandom;

@ListenerMetadata(config = "events.multiply.slime-split")
public class SlimeListener implements Listener {

    private final StackMob sm;
    public SlimeListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler
    public void onSlimeSplit(SlimeSplitEvent event) {
        if (!sm.getEntityManager().isStackedEntity(event.getEntity())) {
            return;
        }
        StackEntity stackEntity = sm.getEntityManager().getStackEntity(event.getEntity());
        if (stackEntity == null || stackEntity.isSingle()) {
            return;
        }
        if (!event.getEntity().hasMetadata(Utilities.SLIME_METADATA)) {
            return;
        }
        int deathAmount = event.getEntity().getMetadata(Utilities.SLIME_METADATA).get(0).asInt();
        for (int i = 0; i < deathAmount; i++) {
            int randSlime = ThreadLocalRandom.current().nextInt(2,4);
            event.setCount(event.getCount() + randSlime);
        }
        event.getEntity().removeMetadata(Utilities.SLIME_METADATA, sm);
    }
}
