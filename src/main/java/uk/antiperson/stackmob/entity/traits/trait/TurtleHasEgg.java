package uk.antiperson.stackmob.entity.traits.trait;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Turtle;
import uk.antiperson.stackmob.entity.traits.Trait;
import uk.antiperson.stackmob.entity.traits.TraitMetadata;

@TraitMetadata(entity = Turtle.class, path = "has-egg")
public class TurtleHasEgg implements Trait {

    @Override
    public boolean checkTrait(LivingEntity first, LivingEntity nearby) {
        return ((Turtle) first).hasEgg() || ((Turtle) nearby).hasEgg();
    }

    @Override
    public void applyTrait(LivingEntity spawned, LivingEntity dead) {
        ((Turtle) spawned).setHasEgg(((Turtle) spawned).hasEgg());
    }
}