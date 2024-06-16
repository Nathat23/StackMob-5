package uk.antiperson.stackmob.entity.traits.trait;

import org.bukkit.entity.Turtle;
import uk.antiperson.stackmob.entity.traits.Trait;
import uk.antiperson.stackmob.entity.traits.TraitMetadata;

@TraitMetadata(path = "has-egg")
public class TurtleHasEgg implements Trait<Turtle> {

    @Override
    public boolean checkTrait(Turtle first, Turtle nearby) {
        return first.hasEgg() || nearby.hasEgg();
    }

    @Override
    public void applyTrait(Turtle spawned, Turtle dead) {
        spawned.setHasEgg(spawned.hasEgg());
    }
}