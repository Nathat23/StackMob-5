package uk.antiperson.stackmob.entity;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.persistence.PersistentDataType;
import uk.antiperson.stackmob.StackMob;

import java.util.Collection;
import java.util.Map;

public class EntityManager {

    private final StackMob sm;
    private final Map<Integer, StackEntity> stackEntities;
    public EntityManager(StackMob sm) {
        this.sm = sm;
        stackEntities = new Object2ObjectOpenHashMap<>();
    }

    public boolean isStackedEntity(LivingEntity entity) {
        return entity.getPersistentDataContainer().has(sm.getStackKey(), PersistentDataType.INTEGER);
    }

    public Collection<StackEntity> getStackEntities() {
        return stackEntities.values();
    }

    public StackEntity getStackEntity(LivingEntity entity) {
        return stackEntities.get(entity.getEntityId());
    }

    public void registerAllEntities() {
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                registerStackedEntities(chunk);
            }
        }
    }

    public void unregisterAllEntities() {
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                unregisterStackedEntities(chunk);
            }
        }
    }

    public void registerStackedEntities(Chunk chunk) {
        for (Entity entity : chunk.getEntities()) {
            if (!(entity instanceof Mob)) {
                continue;
            }
            if (!StackMob.getEntityManager().isStackedEntity((LivingEntity) entity)) {
                continue;
            }
            StackMob.getEntityManager().registerStackedEntity((LivingEntity) entity);
        }
    }

    public void unregisterStackedEntities(Chunk chunk) {
        for (Entity entity : chunk.getEntities()) {
            if (!(entity instanceof Mob)) {
                continue;
            }
            if (!StackMob.getEntityManager().isStackedEntity((LivingEntity) entity)) {
                continue;
            }
            StackMob.getEntityManager().unregisterStackedEntity((LivingEntity) entity);
        }
    }

    public StackEntity registerStackedEntity(LivingEntity entity) {
        StackEntity stackEntity = new StackEntity(sm, entity);
        stackEntities.put(entity.getEntityId(), stackEntity);
        return stackEntity;
    }

    public void registerStackedEntity(StackEntity entity) {
        stackEntities.put(entity.getEntity().getEntityId(), entity);
    }

    public void unregisterStackedEntity(LivingEntity entity) {
        StackEntity stackEntity = stackEntities.remove(entity.getEntityId());
        if (stackEntity == null) {
            throw new UnsupportedOperationException("Attempted to unregister entity that isn't stacked!");
        }
    }

    public void unregisterStackedEntity(StackEntity stackEntity) {
        stackEntities.remove(stackEntity.getEntity().getEntityId());
    }

}
