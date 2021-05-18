package uk.antiperson.stackmob.entity.traits.trait;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import uk.antiperson.stackmob.entity.traits.Trait;
import uk.antiperson.stackmob.entity.traits.TraitMetadata;

@TraitMetadata(entity = Villager.class, path = "villager-profession")
public class VillagerProfession implements Trait {

    @Override
    public boolean checkTrait(LivingEntity first, LivingEntity nearby) {
        return ((Villager) first).getProfession() != ((Villager) nearby).getProfession();
    }

    @Override
    public void applyTrait(LivingEntity spawned, LivingEntity dead) {
        ((Villager) spawned).setProfession(((Villager) dead).getProfession());
    }
}
