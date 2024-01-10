package uk.antiperson.stackmob.entity.traits.trait;

import org.bukkit.entity.Cat;
import org.bukkit.entity.LivingEntity;
import org.checkerframework.checker.units.qual.C;
import uk.antiperson.stackmob.entity.traits.Trait;
import uk.antiperson.stackmob.entity.traits.TraitMetadata;

@TraitMetadata(path = "cat-type")
public class CatType implements Trait<Cat> {

    @Override
    public boolean checkTrait(Cat first, Cat nearby) {
        return first.getCatType() != nearby.getCatType();
    }

    @Override
    public void applyTrait(Cat spawned, Cat dead) {
        spawned.setCatType(dead.getCatType());
    }
}
