package uk.antiperson.stackmob.entity.traits.trait;

import org.bukkit.entity.Mob;
import uk.antiperson.stackmob.entity.traits.Trait;
import uk.antiperson.stackmob.entity.traits.TraitMetadata;

@TraitMetadata(path = "leashed")
public class Leash implements Trait<Mob> {

    @Override
    public boolean checkTrait(Mob first, Mob nearby) {
        if (first.isLeashed() != nearby.isLeashed()) {
            return true;
        }
        return first.isLeashed() && first.getLeashHolder() != nearby.getLeashHolder();
    }

    @Override
    public void applyTrait(Mob spawned, Mob dead) {
        if (!dead.isLeashed()) {
            return;
        }
        spawned.setLeashHolder(dead.getLeashHolder());
    }
}
