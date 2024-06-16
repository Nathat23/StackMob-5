package uk.antiperson.stackmob.entity.traits.trait;

import org.bukkit.entity.Ageable;
import uk.antiperson.stackmob.entity.traits.Trait;
import uk.antiperson.stackmob.entity.traits.TraitMetadata;

@TraitMetadata(path = "age")
public class Age implements Trait<Ageable> {

    @Override
    public boolean checkTrait(Ageable first, Ageable nearby) {
        return first.isAdult() != nearby.isAdult();
    }

    @Override
    public void applyTrait(Ageable spawned, Ageable dead) {
        spawned.setAge(dead.getAge());
    }
}
