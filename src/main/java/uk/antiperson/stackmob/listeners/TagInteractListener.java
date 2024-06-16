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
import uk.antiperson.stackmob.config.EntityConfig;
import uk.antiperson.stackmob.entity.StackEntity;

@ListenerMetadata(config = "events.nametag.enabled")
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
        EntityConfig.NameTagInteractMode nameTagInteractMode = stackEntity.getEntityConfig().getNameTagInteractMode();
        switch (nameTagInteractMode) {
            case PREVENT:
                event.setCancelled(true);
                break;
            case SLICE:
                if (!stackEntity.isSingle()) {
                    stackEntity.slice();
                }
                stackEntity.removeStackData();
                break;
        }
    }
}
