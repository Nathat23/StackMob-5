package uk.antiperson.stackmob.entity.traits.trait;

import org.bukkit.entity.Allay;
import org.bukkit.entity.memory.MemoryKey;
import uk.antiperson.stackmob.entity.traits.Trait;
import uk.antiperson.stackmob.entity.traits.TraitMetadata;

@TraitMetadata(path = "allay-owner")
public class AllayOwner implements Trait<Allay> {

    @Override
    public boolean checkTrait(Allay first, Allay nearby) {
        return first.getMemory(MemoryKey.LIKED_PLAYER) != nearby.getMemory(MemoryKey.LIKED_PLAYER);
    }

    @Override
    public void applyTrait(Allay spawned, Allay dead) {
        spawned.setMemory(MemoryKey.LIKED_PLAYER, dead.getMemory(MemoryKey.LIKED_PLAYER));
    }
}
