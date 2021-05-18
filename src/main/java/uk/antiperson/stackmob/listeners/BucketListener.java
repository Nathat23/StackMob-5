package uk.antiperson.stackmob.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Fish;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;

@ListenerMetadata(config = "events.divide.bucket-fill")
public class BucketListener implements Listener {

    private final StackMob sm;

    public BucketListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBucketFill(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (!(event.getRightClicked() instanceof Fish)) {
            return;
        }
        final ItemStack handItem = event.getPlayer().getInventory().getItemInMainHand();
        if (handItem.getType() != Material.WATER_BUCKET || !handItem.hasItemMeta() || !handItem.getItemMeta().hasDisplayName()) {
            return;
        }
        final StackEntity stackEntity = sm.getEntityManager().getStackEntity((LivingEntity) event.getRightClicked());
        if (stackEntity == null) {
            return;
        }
        if (!stackEntity.isSingle()) {
            stackEntity.slice();
        }
    }

}
