package uk.antiperson.stackmob.entity.traits;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Piglin;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.entity.traits.trait.*;
import uk.antiperson.stackmob.utils.Utilities;

import java.util.HashSet;

public class TraitManager {

    private final HashSet<Trait> traits;
    private final StackMob sm;
    public TraitManager(StackMob sm) {
        this.sm = sm;
        this.traits = new HashSet<>();
    }

    public void registerTraits() throws InstantiationException, IllegalAccessException {
        registerTrait(SheepColor.class);
        registerTrait(SheepShear.class);
        registerTrait(HorseColor.class);
        registerTrait(SlimeSize.class);
        registerTrait(LlamaColor.class);
        registerTrait(ParrotVariant.class);
        registerTrait(CatType.class);
        registerTrait(MooshroomVariant.class);
        registerTrait(FoxType.class);
        registerTrait(Age.class);
        registerTrait(BreedMode.class);
        registerTrait(LoveMode.class);
        registerTrait(DrownedItem.class);
        registerTrait(ZombieBaby.class);
        registerTrait(BeeNectar.class);
        registerTrait(BeeStung.class);
        registerTrait(Leash.class);
        registerTrait(Potion.class);
        registerTrait(VillagerProfession.class);
        if (!Utilities.isNewBukkit()) {
            return;
        }
        registerTrait(ZoglinBaby.class);
        registerTrait(PiglinBaby.class);
        if (Utilities.isPaper()) {
            registerTrait(TurtleHasEgg.class);
        }
    }

    /**
     * If a class hasn't been disabled in the config, add this to the hashset so it can be looped over.
     *
     * TODO: Perhaps there could be a hashset which contains a list of entity types that should be checked.
     * @param trait class that implements trait
     * @throws IllegalAccessException if class is not accessible
     * @throws InstantiationException if class can not be instantiated
     */
    private void registerTrait(Class<? extends Trait> trait) throws IllegalAccessException, InstantiationException {
        TraitMetadata traitMetadata = trait.getAnnotation(TraitMetadata.class);
        if (sm.getMainConfig().isTraitEnabled(traitMetadata.path()) || sm.getMainConfig().getBoolean(traitMetadata.path())) {
            traits.add(trait.newInstance());
        }
    }

    /**
     * Check if the two given entities have any non-matching characteristics which prevent stacking.
     * @param first 1st entity to check
     * @param nearby entity to compare with
     * @return if these entities have any not matching characteristics (traits.)
     */
    public boolean checkTraits(StackEntity first, StackEntity nearby) {
        for (Trait trait : traits) {
            if (isTraitApplicable(trait, first.getEntity())) {
                if (trait.checkTrait(first.getEntity(), nearby.getEntity())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Apply the characteristics of the dead entity to the newly spawned entity.
     * @param spawned the entity that the traits should be copied to.
     * @param dead the entity which traits should be copied from.
     */
    public void applyTraits(StackEntity spawned, StackEntity dead) {
        for (Trait trait : traits) {
            if (isTraitApplicable(trait, spawned.getEntity())) {
                trait.applyTrait(spawned.getEntity(), dead.getEntity());
            }
        }
    }

    /**
     * Check if the trait is applicable to the given entity.
     * @param trait the trait to check.
     * @param entity the entity to check.
     * @return if the trait is applicable to the given entity.
     */
    private boolean isTraitApplicable(Trait trait, LivingEntity entity) {
        TraitMetadata traitMetadata = trait.getClass().getAnnotation(TraitMetadata.class);
        return traitMetadata.entity().isAssignableFrom(entity.getClass());
    }
}
