package uk.antiperson.stackmob.listeners;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;

@ListenerMetadata(config = "events.divide.nametag")
public class TagInteractListener implements Listener {

    private final StackMob sm;
    public TagInteractListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler
    public void onTagInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (!(event.getRightClicked() instanceof Mob)) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }
        ItemStack handItem = event.getPlayer().getInventory().getItemInMainHand();
        if (handItem.getType() != Material.NAME_TAG || !handItem.hasItemMeta() || !handItem.getItemMeta().hasDisplayName()) {
            return;
        }
        StackEntity stackEntity = sm.getEntityManager().getStackEntity((LivingEntity) event.getRightClicked());
        if (stackEntity == null) {
            return;
        }
        if (!stackEntity.isSingle()) {
            stackEntity.slice();
        }
        if (sm.getMainConfig().removeStackDataOnDivide("nametag")) {
            stackEntity.removeStackData();
        }
    }
}
