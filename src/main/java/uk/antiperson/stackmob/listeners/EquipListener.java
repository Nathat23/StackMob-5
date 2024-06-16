package uk.antiperson.stackmob.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;

import java.util.Arrays;
import java.util.List;

@ListenerMetadata(config = "events.equip.enabled")
public class EquipListener implements Listener {

    private final StackMob sm;

    public EquipListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler
    public void onEntityEquip(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Mob)) {
            return;
        }
        if (!isArmour(event.getItem().getItemStack().getType())) {
            return;
        }
        if (!sm.getEntityManager().isStackedEntity(event.getEntity())) {
            return;
        }
        StackEntity stackEntity = sm.getEntityManager().getStackEntity(event.getEntity());
        stackEntity.addEquipItem(event.getItem().getItemStack());
    }

    private boolean isArmour(Material material) {
        List<String> endings = Arrays.asList("HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS", "AXE", "SHOVEL", "HOE", "SWORD", "SHIELD");
        for (String ending : endings) {
            if (material.toString().endsWith(ending)) {
                return true;
            }
        }
        return false;
    }
}
