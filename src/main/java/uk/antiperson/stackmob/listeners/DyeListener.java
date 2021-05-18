package uk.antiperson.stackmob.listeners;

import org.bukkit.DyeColor;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.utils.Utilities;

@ListenerMetadata(config = "events.dye.enabled")
public class DyeListener implements Listener {

    private final StackMob sm;

    public DyeListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler
    public void onDyeListener(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!(event.getRightClicked() instanceof Sheep)) {
            return;
        }
        ItemStack handItem = event.getPlayer().getInventory().getItemInMainHand();
        if (!Utilities.isDye(handItem)) {
            return;
        }
        Sheep sheep = (Sheep) event.getRightClicked();
        StackEntity stackEntity = sm.getEntityManager().getStackEntity(sheep);
        if (stackEntity == null || stackEntity.isSingle()) {
            return;
        }
        ListenerMode mode = sm.getMainConfig().getListenerMode(sheep.getType(), "dye");
        if (mode == ListenerMode.SPLIT) {
            ((Colorable) stackEntity.slice().getEntity()).setColor(sheep.getColor());
            return;
        }
        stackEntity.splitIfNotEnough(event.getPlayer().getInventory().getItemInMainHand().getAmount());
        int limit = sm.getMainConfig().getEventMultiplyLimit(sheep.getType(), "dye", stackEntity.getSize());
        if (stackEntity.getSize() > limit) {
            stackEntity.slice(limit);
        }
        Utilities.removeHandItem(event.getPlayer(), stackEntity.getSize());
        sheep.setColor(DyeColor.valueOf(handItem.getType().toString().replace("_DYE", "")));
    }
}
