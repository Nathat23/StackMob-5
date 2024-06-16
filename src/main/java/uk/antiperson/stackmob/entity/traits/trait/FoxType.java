package uk.antiperson.stackmob.entity.traits.trait;

import org.bukkit.entity.Fox;
import uk.antiperson.stackmob.entity.traits.Trait;
import uk.antiperson.stackmob.entity.traits.TraitMetadata;

@TraitMetadata(path = "fox-type")
public class FoxType implements Trait<Fox> {

    @Override
    public boolean checkTrait(Fox first, Fox nearby) {
        return first.getFoxType() != nearby.getFoxType();
    }

    @Override
    public void applyTrait(Fox spawned, Fox dead) {
        spawned.setFoxType(dead.getFoxType());
    }
}
