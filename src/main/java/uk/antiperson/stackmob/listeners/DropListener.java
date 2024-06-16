package uk.antiperson.stackmob.listeners;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.Drops;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.utils.Utilities;

import java.util.concurrent.ThreadLocalRandom;

@ListenerMetadata(config = "events.multiply.drops")
public class DropListener implements Listener {

    private final StackMob sm;

    public DropListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler
    public void onDropListener(EntityDropItemEvent event) {
        if (!(event.getEntity() instanceof Villager) &&
                event.getItemDrop().getItemStack().getType() != Material.EGG &&
                event.getItemDrop().getItemStack().getType() != Utilities.getScuteMaterial()) {
            return;
        }
        if (!sm.getEntityManager().isStackedEntity((LivingEntity) event.getEntity())) {
            return;
        }
        StackEntity entity = sm.getEntityManager().getStackEntity((LivingEntity) event.getEntity());
        double multiplier = ThreadLocalRandom.current().nextDouble(0.4, 0.8);
        int itemCount = (int) Math.round((entity.getSize() - 1) * multiplier);
        Drops.dropItem(event.getEntity().getLocation(), event.getItemDrop().getItemStack(), itemCount);
    }
}
