package uk.antiperson.stackmob.entity.traits.trait;

import org.bukkit.entity.Llama;
import uk.antiperson.stackmob.entity.traits.Trait;
import uk.antiperson.stackmob.entity.traits.TraitMetadata;

@TraitMetadata(path = "llama-color")
public class LlamaColor implements Trait<Llama> {

    @Override
    public boolean checkTrait(Llama first, Llama nearby) {
        return first.getColor() != nearby.getColor();
    }

    @Override
    public void applyTrait(Llama spawned, Llama dead) {
        spawned.setColor(dead.getColor());
    }
}
