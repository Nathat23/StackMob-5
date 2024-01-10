package uk.antiperson.stackmob.entity.traits.trait;

import org.bukkit.entity.Slime;
import uk.antiperson.stackmob.entity.traits.Trait;
import uk.antiperson.stackmob.entity.traits.TraitMetadata;

@TraitMetadata(path = "slime-size")
public class SlimeSize implements Trait<Slime> {

    @Override
    public boolean checkTrait(Slime first, Slime nearby) {
        return first.getSize() != nearby.getSize();
    }

    @Override
    public void applyTrait(Slime spawned, Slime dead) {
        spawned.setSize(dead.getSize());
    }
}
