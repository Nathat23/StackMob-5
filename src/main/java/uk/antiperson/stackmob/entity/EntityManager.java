package uk.antiperson.stackmob.entity;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.persistence.PersistentDataType;
import uk.antiperson.stackmob.StackMob;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EntityManager {

    private final StackMob sm;
    private final Map<UUID, StackEntity> stackEntities;

    public EntityManager(StackMob sm) {
        this.sm = sm;
        stackEntities = new ConcurrentHashMap<>();
    }

    public boolean isStackedEntity(LivingEntity entity) {
        return stackEntities.containsKey(entity.getUniqueId());
    }

    public Collection<StackEntity> getStackEntities() {
        return stackEntities.values();
    }

    public StackEntity getStackEntity(LivingEntity entity) {
        return stackEntities.get(entity.getUniqueId());
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

    public boolean hasStackData(Entity entity) {
        return entity.getPersistentDataContainer().has(sm.getStackKey(), PersistentDataType.INTEGER);
    }

    public void registerStackedEntities(Chunk chunk) {
        for (Entity entity : chunk.getEntities()) {
            if (!(entity instanceof Mob)) {
                continue;
            }
            if (!hasStackData(entity)) {
                continue;
            }
            sm.getEntityManager().registerStackedEntity((LivingEntity) entity);
        }
    }

    public void unregisterStackedEntities(Chunk chunk) {
        for (Entity entity : chunk.getEntities()) {
            if (!(entity instanceof Mob)) {
                continue;
            }
            StackEntity stackEntity = sm.getEntityManager().getStackEntity((LivingEntity) entity);
            if (stackEntity == null) {
                continue;
            }
            sm.getEntityManager().unregisterStackedEntity(stackEntity);
        }
    }

    public StackEntity registerStackedEntity(LivingEntity entity) {
        StackEntity stackEntity = new StackEntity(sm, entity);
        stackEntities.put(entity.getUniqueId(), stackEntity);
        return stackEntity;
    }

    public void registerStackedEntity(StackEntity entity) {
        stackEntities.put(entity.getEntity().getUniqueId(), entity);
    }

    public void unregisterStackedEntity(LivingEntity entity) {
        StackEntity stackEntity = getStackEntity(entity);
        if (stackEntity == null) {
            throw new UnsupportedOperationException("Attempted to unregister entity that isn't stacked!");
        }
        unregisterStackedEntity(stackEntity);
    }

    public void unregisterStackedEntity(StackEntity stackEntity) {
        stackEntities.remove(stackEntity.getEntity().getUniqueId());
    }

}
