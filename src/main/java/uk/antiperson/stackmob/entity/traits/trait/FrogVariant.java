package uk.antiperson.stackmob.entity.traits.trait;

import org.bukkit.entity.Frog;
import org.bukkit.entity.LivingEntity;
import uk.antiperson.stackmob.entity.traits.Trait;
import uk.antiperson.stackmob.entity.traits.TraitMetadata;

@TraitMetadata(path = "frog-variant")
public class FrogVariant implements Trait<Frog> {

    @Override
    public boolean checkTrait(Frog first, Frog nearby) {
        return first.getVariant() != nearby.getVariant();
    }

    @Override
    public void applyTrait(Frog spawned, Frog dead) {
        spawned.setVariant(dead.getVariant());
    }
}
