package uk.antiperson.stackmob.entity.traits.trait;

import org.bukkit.entity.Zoglin;
import uk.antiperson.stackmob.entity.traits.Trait;
import uk.antiperson.stackmob.entity.traits.TraitMetadata;

@TraitMetadata(path = "zoglin-baby")
public class ZoglinBaby implements Trait<Zoglin> {

    @Override
    public boolean checkTrait(Zoglin first, Zoglin nearby) {
        return first.isBaby() != nearby.isBaby();
    }

    @Override
    public void applyTrait(Zoglin spawned, Zoglin dead) {
        spawned.setBaby(dead.isBaby());
    }
}
