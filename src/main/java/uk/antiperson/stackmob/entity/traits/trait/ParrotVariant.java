package uk.antiperson.stackmob.entity.traits.trait;

import org.bukkit.entity.Parrot;
import uk.antiperson.stackmob.entity.traits.Trait;
import uk.antiperson.stackmob.entity.traits.TraitMetadata;

@TraitMetadata(path = "parrot-variant")
public class ParrotVariant implements Trait<Parrot> {

    @Override
    public boolean checkTrait(Parrot first, Parrot nearby) {
        return first.getVariant() != nearby.getVariant();
    }

    @Override
    public void applyTrait(Parrot spawned, Parrot dead) {
        spawned.setVariant(spawned.getVariant());
    }
}
