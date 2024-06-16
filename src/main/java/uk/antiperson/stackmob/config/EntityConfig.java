package uk.antiperson.stackmob.config;

import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Raider;
import org.bukkit.entity.WaterMob;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityKnockbackEvent;
import org.stringtemplate.v4.ST;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.entity.death.DeathType;
import uk.antiperson.stackmob.hook.hooks.JobsHook;
import uk.antiperson.stackmob.utils.Utilities;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EntityConfig {

    private final Map<String, ConfigValue> valueMap;
    private final EntityType type;
    private final StackMob sm;

    public EntityConfig(StackMob sm, EntityType type) {
        this.sm = sm;
        this.type = type;
        this.valueMap = new HashMap<>();
    }

    public void put(String path, ConfigValue value) {
        valueMap.put(path, value);
    }

    public ConfigValue getConfigValue(String path) {
        ConfigValue configValue = valueMap.get(path);
        if (configValue == null) {
            sm.getLogger().info("The value at config path " + path + " is null. Make sure your config is correctly formatted at this path.");
        }
        return configValue;
    }

    public boolean getBoolean(String path) {
        ConfigValue configValue = getConfigValue(path);
        return configValue != null && configValue.getBoolean();
    }

    public int getInt(String path) {
        ConfigValue configValue = getConfigValue(path);
        return configValue == null ? 0 : configValue.getInt();
    }

    public double getDouble(String path) {
        ConfigValue configValue = getConfigValue(path);
        return configValue == null ? 0 : configValue.getDouble();
    }

    public String getString(String path) {
        ConfigValue configValue = getConfigValue(path);
        return configValue == null ? "" : configValue.getString();
    }

    public List<Integer> getIntList(String path) {
        ConfigValue configValue = getConfigValue(path);
        return configValue == null ? Collections.emptyList() : configValue.asIntList();
    }

    public ConfigList getList(String path) {
        ConfigValue configValue = getConfigValue(path);
        return configValue == null ? new ConfigList(null, Collections.emptyList(), path, false) : configValue.getList();
    }

    public int getMaxStack() {
        return getInt("stack.max-size");
    }

    public boolean getStackThresholdEnabled() {
        return getBoolean( "stack.threshold.enabled");
    }

    public int getStackThreshold() {
        return getInt( "stack.threshold.amount");
    }

    public Integer[] getStackRadius() {
        return getIntList("stack.merge-range").toArray(new Integer[2]);
    }

    public int getStackInterval() {
        return getInt("stack.interval");
    }

    public boolean isCheckHasMoved() {
        return getBoolean("stack.check-location.enabled");
    }

    public double getCheckHasMovedDistance() {
        return getDouble("stack.check-location.distance");
    }

    public int getCheckHasMovedTimeout() {
        return getInt("stack.check-location.timeout");
    }

    public String getTagFormat() {
        return getString("display-name.format");
    }

    public int getTagThreshold() {
        return getInt( "display-name.threshold");
    }

    public StackEntity.TagMode getTagMode() {
        StackEntity.TagMode tagMode = StackEntity.TagMode.valueOf(getString("display-name.visibility"));
        if (sm.getHookManager().getProtocolLibHook() == null && tagMode == StackEntity.TagMode.NEARBY) {
            tagMode = StackEntity.TagMode.HOVER;
        }
        return tagMode;
    }

    public Integer[] getTagNearbyRadius() {
        return getIntList("display-name.nearby.range").toArray(new Integer[2]);
    }

    public int getTagNearbyInterval() {
        return getInt("display-name.nearby.interval");
    }

    public boolean isTagNearbyRayTrace() {
        return getBoolean("display-name.nearby.ray-trace");
    }

    public boolean isUseArmorStand() {
        return getBoolean("display-name.nearby.armorstand.enabled");
    }

    public double getArmorstandOffset() {
        return getDouble("display-name.nearby.armorstand.offset");
    }

    public boolean isTraitEnabled(String traitKey) {
        return getBoolean("traits." + traitKey);
    }

    public boolean isHookEnabled(String traitKey) {
        return getBoolean("hooks." + traitKey);
    }

    public JobsHook.JobHookMode getJobHookMode() {
        return JobsHook.JobHookMode.valueOf(getString("hooks.jobs.mode"));
    }

    public boolean isDropMultiEnabled() {
        return getBoolean( "drops.enabled");
    }

    public boolean isDropLootTables() {
        return getBoolean( "drops.use-loot-tables");
    }

    public boolean isSlimeMultiEnabled() {
        return getBoolean( "events.multiply.slime-split");
    }

    public boolean isDropTypeBlacklist() {
        return isEntityTypeInList("drops.type-blacklist");
    }

    public boolean isDropReasonBlacklist(EntityDamageEvent.DamageCause damageCause) {
        return getList("drops.reason-blacklist").contains(damageCause.toString());
    }

    public ConfigList getDropItemBlacklist() {
        return getList("drops.item-blacklist");
    }

    public ConfigList getDropItemOnePer() {
        return getList("drops.one-per-stack");
    }

    public boolean isExpMultiEnabled() {
        return getBoolean( "experience.enabled");
    }

    public boolean isExpTypeBlacklist() {
        return getList("experience.type-blacklist").contains(type.toString());
    }

    public double getExpMinBound() {
        return getDouble("experience.multiplier-min");
    }

    public double getExpMaxBound() {
        return getDouble("experience.multiplier-max");
    }

    public boolean isPlayerStatMulti() {
        return getBoolean( "player-stats");
    }

    public boolean isWaitingEnabled() {
        return getBoolean( "wait-to-stack.enabled");
    }

    public boolean isWaitingTypes() {
        return isEntityTypeInList("wait-to-stack.types-whitelist");
    }

    public boolean isWaitingReasons(CreatureSpawnEvent.SpawnReason spawnReason) {
        return getList("wait-to-stack.reasons-whitelist").contains(spawnReason.toString());
    }

    public int getWaitingTime() {
        return getInt("wait-to-stack.wait-time");
    }

    public int getMaxDeathStep() {
        return getInt( "death.STEP.max-step");
    }

    public int getMinDeathStep() {
        return getInt( "death.STEP.min-step");
    }

    public boolean isTargetingDisabledTypes() {
        return isEntityTypeInList("disable-targeting.type-blacklist");
    }

    public boolean isTargetingDisabledReasons(CreatureSpawnEvent.SpawnReason spawnReason) {
        return getList("disable-targeting.reason-blacklist").contains(spawnReason.toString());
    }

    public boolean isKnockbackDisabledTypes() {
        return isEntityTypeInList("disable-knockback.type-blacklist");
    }

    public boolean isKnockbackDisabledReasons(CreatureSpawnEvent.SpawnReason spawnReason) {
        return getList("disable-knockback.reason-blacklist").contains(spawnReason.toString());
    }

    public boolean isKnockbackDisabledCause(EntityKnockbackEvent.KnockbackCause cause) {
        return getList("disable-knockback.cause-blacklist").contains(cause.toString());
    }

    public ListenerMode getListenerMode(EventType eventType) {
        return ListenerMode.valueOf(getString("events." + eventType.getConfigKey() + ".mode"));
    }

    public int getEventMultiplyLimit(EventType eventType, int stackSize) {
        int limit = getInt( "events." + eventType.getConfigKey() + ".limit");
        return limit == -1 ? stackSize : Math.min(stackSize, limit);
    }

    public boolean isWorldBlacklisted(World world) {
        return getList("worlds-blacklist").contains(world.getName());
    }

    public boolean isEntityBlacklisted(LivingEntity entity) {
        CreatureSpawnEvent.SpawnReason reason = Utilities.isPaper() ? entity.getEntitySpawnReason() : CreatureSpawnEvent.SpawnReason.DEFAULT;
        return isEntityBlacklisted(entity, reason);
    }

    public boolean isEntityBlacklisted(LivingEntity entity, CreatureSpawnEvent.SpawnReason reason) {
        if (isEntityTypeInList("types-blacklist")) {
            return true;
        }
        if (getList("reason-blacklist").contains(reason.toString())) {
            return true;
        }
        return isWorldBlacklisted(entity.getWorld());
    }

    public DeathType getDeathType(LivingEntity dead) {
        for (DeathType type : getDeathSection()) {
            ConfigList reasons = getList("death." + type + ".reason-blacklist");
            if (dead.getLastDamageCause() != null && reasons.contains(dead.getLastDamageCause().getCause().toString())) {
                continue;
            }
            ConfigList spawnReasons = getList("death." + type + ".spawn-reason-blacklist");
            if (Utilities.isPaper() && spawnReasons.contains(dead.getEntitySpawnReason().toString())) {
                continue;
            }
            if (isEntityTypeInList("death." + type + ".type-blacklist")) {
                continue;
            }
            return type;
        }
        throw new UnsupportedOperationException("Configuration error - unable to determine death type!");
    }

    private Collection<DeathType> getDeathSection() {
        TreeMap<Integer, DeathType> array = new TreeMap<>();
        for (DeathType type : DeathType.values()) {
            array.put(getInt("death." + type + ".priority"), type);
        }
        return array.values();
    }

    public boolean isSkipDeathAnimation() {
        return getBoolean( "death.skip-animation") && Utilities.isPaper();
    }

    public StackEntity.EquipItemMode getEquipItemMode() {
        return StackEntity.EquipItemMode.valueOf(getString("events.equip.mode"));
    }

    public boolean isStackOnSpawn() {
        return getBoolean("stack.on-spawn");
    }

    private boolean isEntityTypeInList(String path) {
        ConfigList list = getList(path);
        if (list.isInverted() && list.rawContains(type.toString())) {
            return false;
        }
        for (EntityGrouping entityGrouping : EntityGrouping.values()) {
            if (!list.rawContains(entityGrouping.toString())) {
                continue;
            }
            if (entityGrouping.isEntityMemberOf(type.getEntityClass())) {
                return !list.isInverted();
            }
        }
        return list.contains(type.toString());
    }

    public NameTagInteractMode getNameTagInteractMode() {
        return NameTagInteractMode.valueOf(getString("events.nametag.mode"));
    }

    public NameTagStackMode getNameTagStackMode() {
        NameTagStackMode nameTagStackMode = NameTagStackMode.valueOf(getString("stack.nametag-mode"));
        if (nameTagStackMode == NameTagStackMode.JOIN && !(getTagMode() == StackEntity.TagMode.NEARBY && isUseArmorStand())) {
            return NameTagStackMode.IGNORE;
        }
        return nameTagStackMode;
    }

    public boolean isCheckCanSee() {
        return getBoolean("stack.line-of-sight");
    }

    public EntityType getType() {
        return type;
    }

    enum EntityGrouping {
        HOSTILE(Monster.class, Ghast.class, Phantom.class),
        ANIMALS(Animals.class),
        WATER(WaterMob.class),
        RAIDER(Raider.class),
        BOSS(Boss.class);

        final Class<? extends Entity>[] classes;
        @SafeVarargs
        EntityGrouping(Class<? extends Entity>... classes) {
            this.classes = classes;
        }

        public boolean isEntityMemberOf(Class<? extends Entity> entity) {
            if (entity == null) {
                return false;
            }
            for (Class<? extends Entity> entityClass : classes) {
                if (entityClass.isAssignableFrom(entity)) {
                    return true;
                }
            }
            return false;
        }
    }

    public enum EventType {
        BREED("breed"),
        DYE("dye"),
        EXPLOSION("explosion"),
        SHEAR("shear");

        final String configKey;
        EventType(String configKey) {
            this.configKey = configKey;
        }

        public String getConfigKey() {
            return configKey;
        }
    }

    public enum ListenerMode {
        MULTIPLY,
        SPLIT
    }

    public enum NameTagInteractMode {
        SLICE,
        PREVENT
    }

    public enum NameTagStackMode {
        IGNORE,
        DROP,
        JOIN
    }
}
