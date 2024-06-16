package uk.antiperson.stackmob.entity.traits.trait;

import org.bukkit.entity.Horse;
import uk.antiperson.stackmob.entity.traits.Trait;
import uk.antiperson.stackmob.entity.traits.TraitMetadata;

@TraitMetadata(path = "horse-color")
public class HorseColor implements Trait<Horse> {

    @Override
    public boolean checkTrait(Horse first, Horse nearby) {
        return (first.getColor() != nearby.getColor()) ||
                (first.getStyle() != nearby.getStyle());
    }

    @Override
    public void applyTrait(Horse spawned, Horse dead) {
        spawned.setColor(dead.getColor());
        spawned.setStyle(spawned.getStyle());
    }
}
