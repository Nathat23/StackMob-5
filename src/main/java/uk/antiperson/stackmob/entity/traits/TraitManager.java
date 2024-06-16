package uk.antiperson.stackmob.entity.traits;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.entity.traits.trait.*;
import uk.antiperson.stackmob.utils.Utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TraitManager {

    private final Map<EntityType, Set<Trait<LivingEntity>>> traits;
    private final StackMob sm;
    public TraitManager(StackMob sm) {
        this.sm = sm;
        this.traits = new EnumMap<>(EntityType.class);
    }

    public void registerTraits() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
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
        if (Utilities.isPaper()) {
            registerTrait(TurtleHasEgg.class);
        }
        registerTrait(ZoglinBaby.class);
        registerTrait(PiglinBaby.class);
        if (Utilities.isVersionAtLeast(Utilities.MinecraftVersion.V1_19_4)) {
            registerTrait(FrogVariant.class);
            registerTrait(AllayOwner.class);
        }
    }

    /**
     * If a class hasn't been disabled in the config, add this to the hashset so it can be looped over.
     *
     * @param traitClass class that implements trait
     * @throws IllegalAccessException if class is not accessible
     * @throws InstantiationException if class can not be instantiated
     * @throws NoSuchMethodException if class constructor can not be found
     * @throws InvocationTargetException if instantiation fails
     */
    private <T extends Trait<? extends LivingEntity>> void registerTrait(Class<T> traitClass) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        final TraitMetadata traitMetadata = traitClass.getAnnotation(TraitMetadata.class);
        if (!sm.getMainConfig().getConfig().isTraitEnabled(traitMetadata.path())) {
            return;
        }
        Trait<LivingEntity> trait = (Trait<LivingEntity>) traitClass.getDeclaredConstructor().newInstance();
        ParameterizedType parameterizedType = (ParameterizedType) trait.getClass().getGenericInterfaces()[0];
        Class<? extends LivingEntity> typeArgument = (Class<? extends LivingEntity>) parameterizedType.getActualTypeArguments()[0];
        for (EntityType entityType : EntityType.values()) {
            if (entityType.getEntityClass() != null && typeArgument.isAssignableFrom(entityType.getEntityClass())) {
                traits.computeIfAbsent(entityType, type -> new HashSet<>()).add(trait);
            }
        }
    }

    /**
     * Check if the two given entities have any non-matching characteristics which prevent stacking.
     * @param first 1st entity to check
     * @param nearby entity to compare with
     * @return if these entities have any not matching characteristics (traits.)
     */
    public boolean checkTraits(StackEntity first, StackEntity nearby) {
        Set<Trait<LivingEntity>> set = traits.get(first.getEntity().getType());
        if (set == null) {
            return false;
        }
        for (Trait<LivingEntity> trait : set) {
            if (trait.checkTrait(first.getEntity(), nearby.getEntity())) {
                return true;
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
        Set<Trait<LivingEntity>> set = traits.get(spawned.getEntity().getType());
        if (set == null) {
            return;
        }
        for (Trait<LivingEntity> trait : set) {
            trait.applyTrait(spawned.getEntity(), dead.getEntity());
        }
    }
}
