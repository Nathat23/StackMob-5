package uk.antiperson.stackmob.entity.traits.trait;

import org.bukkit.entity.Bee;
import uk.antiperson.stackmob.entity.traits.Trait;
import uk.antiperson.stackmob.entity.traits.TraitMetadata;

@TraitMetadata(path = "bee-stung")
public class BeeStung implements Trait<Bee> {
    @Override
    public boolean checkTrait(Bee first, Bee nearby) {
        return first.hasStung() != nearby.hasStung();
    }

    @Override
    public void applyTrait(Bee spawned, Bee dead) {
        spawned.setHasStung(dead.hasStung());
    }
}

