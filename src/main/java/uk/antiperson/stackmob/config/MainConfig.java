package uk.antiperson.stackmob.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class MainConfig {

    private final Map<EntityType, EntityConfig> map;
    private final MainConfigFile configFile;
    private final StackMob sm;

    public MainConfig(StackMob sm) {
        this.sm = sm;
        this.map = new EnumMap<>(EntityType.class);
        this.configFile = new MainConfigFile(sm);
    }

    public void init() throws IOException {
        configFile.load();
        // iterate every entity type
        for (EntityType entityType : EntityType.values()) {
            if (entityType.getEntityClass() == null || !Mob.class.isAssignableFrom(entityType.getEntityClass())) {
                if (entityType != EntityType.UNKNOWN) {
                    continue;
                }
            }
            // create new EntityConfig for this entity type
            EntityConfig entityConfig = new EntityConfig(sm, entityType);
            // populate  with config contents
            for (String key : configFile.getKeys(true)) {
                if (key.startsWith("custom")) {
                    continue;
                }
                entityConfig.put(key, new ConfigValue(configFile, key, configFile.get(key)));
            }
            // store
            map.put(entityType, entityConfig);
        }
        // now find which config items have been overridden
        ConfigurationSection custom = configFile.getConfigurationSection("custom");
        if (custom == null) {
            return;
        }
        // get all the top level keys in the custom section
        for (String key : custom.getKeys(false)) {
            EntityType toRead = EntityType.valueOf(key);
            // determine if we are cloning another section
            EntityType cloneType = null;
            String clone = custom.getString(toRead + ".clone", null);
            if (clone != null && clone.length() > 0) {
                cloneType = EntityType.valueOf(clone);
            }
            // get the entity config for this section
            EntityConfig entityConfig = map.get(toRead);
            // load all custom values (from the cloned entity type and this entity type) into the entity config
            EntityType[] array = new EntityType[]{cloneType, toRead}; // order is important - cloned values may be overridden
            for (EntityType type : array) {
                if (type == null) {
                    continue;
                }
                ConfigurationSection section = configFile.getConfigurationSection("custom." + type);
                for (String customKey : section.getKeys(true)) {
                    if (customKey.equals("clone")) {
                        continue;
                    }
                    String actualKey = "custom." + type + "." + customKey;
                    entityConfig.put(customKey, new ConfigValue(configFile, actualKey, section.get(customKey)));
                }
            }
        }
    }

    public void reload() throws IOException {
        init();
        sm.getEntityManager().getStackEntities().forEach(StackEntity::refreshConfig);
    }

    /**
     * Returns the EntityConfig for the specified entity type
     * @param type the type to check
     * @return the EntityConfig for the specified entity type
     */
    public EntityConfig getConfig(EntityType type) {
        return map.get(type);
    }

    /**
     * Returns the EntityConfig for the specified entity
     * @param entity the entity to check
     * @return the EntityConfig for the specified entity
     */
    public EntityConfig getConfig(Entity entity) {
        return getConfig(entity.getType());
    }

    /**
     * Returns the EntityConfig whenever there is no entity present
     * @return the EntityConfig whenever there is no entity present
     */
    public EntityConfig getConfig() {
        return map.get(EntityType.UNKNOWN);
    }

    public MainConfigFile getConfigFile() {
        return configFile;
    }
}
