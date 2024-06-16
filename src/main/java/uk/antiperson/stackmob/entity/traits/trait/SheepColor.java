package uk.antiperson.stackmob.entity.traits.trait;

import org.bukkit.entity.Sheep;
import uk.antiperson.stackmob.entity.traits.Trait;
import uk.antiperson.stackmob.entity.traits.TraitMetadata;

@TraitMetadata(path = "sheep-color")
public class SheepColor implements Trait<Sheep> {

    @Override
    public boolean checkTrait(Sheep first, Sheep nearby) {
        return first.getColor() != nearby.getColor();
    }

    @Override
    public void applyTrait(Sheep spawned, Sheep dead) {
        spawned.setColor(dead.getColor());
    }
}
