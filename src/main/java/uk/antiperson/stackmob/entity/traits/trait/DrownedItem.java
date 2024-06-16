package uk.antiperson.stackmob.entity.traits.trait;

import org.bukkit.entity.Drowned;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import uk.antiperson.stackmob.entity.traits.Trait;
import uk.antiperson.stackmob.entity.traits.TraitMetadata;
import uk.antiperson.stackmob.utils.Utilities;

@TraitMetadata(path = "drowned-hand-item")
public class DrownedItem implements Trait<Drowned> {
    
    @Override
    public boolean checkTrait (Drowned first, Drowned nearby) {
        for (EquipmentSlot equipmentSlot : Utilities.HAND_SLOTS) {
            ItemStack oriItemStack = first.getEquipment().getItem(equipmentSlot);
            ItemStack nearItemStack = nearby.getEquipment().getItem(equipmentSlot);
            if (!oriItemStack.isSimilar(nearItemStack)) {
                continue;
            }
            if (Utilities.DROWNED_MATERIALS.contains(oriItemStack.getType())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void applyTrait(Drowned spawned, Drowned dead) {
        for (EquipmentSlot equipmentSlot : Utilities.HAND_SLOTS) {
            ItemStack item = dead.getEquipment().getItem(equipmentSlot);
            if (Utilities.DROWNED_MATERIALS.contains(item.getType())) {
                spawned.getEquipment().setItem(equipmentSlot, item);
            }
        }
    }
}
