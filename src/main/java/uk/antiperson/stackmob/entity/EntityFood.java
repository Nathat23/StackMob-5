package uk.antiperson.stackmob.entity;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Tameable;
import uk.antiperson.stackmob.utils.Utilities;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Predicate;

public enum EntityFood {
    COW(Material.WHEAT),
    SHEEP(Material.WHEAT),
    MUSHROOM_COW(Material.WHEAT),
    PIG(Material.CARROT, Material.BEETROOT, Material.POTATO),
    CHICKEN(Material.WHEAT_SEEDS, Material.BEETROOT_SEEDS, Material.MELON_SEEDS, Material.PUMPKIN_SEEDS),
    HORSE(Tameable::isTamed, Material.GOLDEN_CARROT, Material.GOLDEN_APPLE),
    WOLF(Tameable::isTamed, Material.BEEF,
            Material.CHICKEN,
            Material.COD,
            Material.MUTTON,
            Material.PORKCHOP,
            Material.RABBIT,
            Material.SALMON,
            Material.COOKED_BEEF,
            Material.COOKED_CHICKEN,
            Material.COOKED_COD,
            Material.COOKED_MUTTON,
            Material.COOKED_PORKCHOP,
            Material.COOKED_RABBIT,
            Material.COOKED_SALMON),
    OCELOT(Material.SALMON, Material.COD, Material.PUFFERFISH, Material.TROPICAL_FISH),
    RABBIT(Material.CARROT, Material.GOLDEN_CARROT, Material.DANDELION),
    LLAMA(Material.HAY_BLOCK),
    TURTLE(Material.SEAGRASS),
    PANDA(Material.BAMBOO),
    FOX(Material.SWEET_BERRIES),
    CAT(Tameable::isTamed, Material.SALMON, Material.COD),
    BEE(Tag.FLOWERS.getValues().toArray(new Material[0])),
    HOGLIN(Material.CRIMSON_FUNGUS),
    INVALID;

    private final Predicate<Tameable> predicate;
    private final Material[] foods;
    EntityFood(Predicate<Tameable> predicate, Material... foods) {
        this.predicate = predicate;
        this.foods = foods;
    }

    EntityFood(Material... foods) {
        this.predicate = null;
        this.foods = foods;
    }

    public Material[] getFoods() {
        return foods;
    }

    public Predicate<Tameable> getPredicate() {
        return predicate;
    }

    public static EntityFood matchFood(EntityType type) {
        try {
            return EntityFood.valueOf(type.toString());
        } catch (IllegalArgumentException e) {
            return EntityFood.INVALID;
        }
    }

    public static boolean isCorrectFood(Entity entity, Material type) {
        if (Utilities.isVersionAtLeast(Utilities.MinecraftVersion.V1_17)) {
            return ((Animals) entity).isBreedItem(type);
        }
        EntityFood food = matchFood(entity.getType());
        if (food.getPredicate() != null && !food.getPredicate().getClass().isAssignableFrom(entity.getClass())) {
            return false;
        }
        for (Material material : food.getFoods()) {
            if (material == type) {
                return true;
            }
        }
        return false;
    }
}
