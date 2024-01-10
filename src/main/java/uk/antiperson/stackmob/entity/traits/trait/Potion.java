package uk.antiperson.stackmob.entity.traits.trait;

import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffect;
import uk.antiperson.stackmob.entity.traits.Trait;
import uk.antiperson.stackmob.entity.traits.TraitMetadata;

@TraitMetadata(path = "potion-effect")
public class Potion implements Trait<Mob> {

    @Override
    public boolean checkTrait(Mob first, Mob nearby) {
        return false;
    }

    @Override
    public void applyTrait(Mob spawned, Mob dead) {
        for (PotionEffect potionEffect : dead.getActivePotionEffects()) {
            spawned.addPotionEffect(potionEffect);
        }
    }
}
