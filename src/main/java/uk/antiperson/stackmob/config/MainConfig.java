package uk.antiperson.stackmob.config;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.entity.death.DeathType;
import uk.antiperson.stackmob.listeners.ListenerMode;
import uk.antiperson.stackmob.utils.Utilities;

import java.io.IOException;
import java.util.*;

public class MainConfig extends SpecialConfigFile {

    private final StackMob sm;
    public MainConfig(StackMob sm) {
        super(sm, "config.yml");
        this.sm = sm;
    }

    public int getMaxStack(EntityType type) {
        return getInt(type, "stack.max-size");
    }

    public boolean getStackThresholdEnabled(EntityType type) {
        return getBoolean(type, "stack.threshold.enabled");
    }

    public int getStackThreshold(EntityType type) {
        return getInt(type, "stack.threshold.amount");
    }

    public Integer[] getStackRadius(EntityType type) {
        return getList(type, "stack.merge-range").asIntList().toArray(new Integer[2]);
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

    public String getTagFormat(EntityType type) {
        return getString(type, "display-name.format");
    }

    public int getTagThreshold(EntityType type) {
        return getInt(type, "display-name.threshold");
    }

    public StackEntity.TagMode getTagMode(EntityType type) {
        return StackEntity.TagMode.valueOf(getString(type, "display-name.visibility"));
    }

    public Integer[] getTagNeabyRadius() {
        return getList("display-name.nearby.range").asIntList().toArray(new Integer[2]);
    }

    public int getTagNearbyInterval() {
        return getInt("display-name.nearby.interval");
    }

    public boolean isTraitEnabled(String traitKey) {
        return getBoolean("traits." + traitKey);
    }

    public boolean isHookEnabled(String traitKey) {
        return getBoolean("hooks." + traitKey);
    }

    public boolean isDropMultiEnabled(EntityType type) {
        return getBoolean(type, "drops.enabled");
    }

    public boolean isDropLootTables(EntityType type) {
        return getBoolean(type, "drops.use-loot-tables");
    }

    public boolean isSlimeMultiEnabled(EntityType type) {
        return getBoolean(type, "multiply.slime-split");
    }

    public ConfigList getDropTypeBlacklist(EntityType type) {
        return getList(type, "drops.type-blacklist");
    }

    public ConfigList getDropReasonBlacklist(EntityType type) {
        return getList(type, "drops.reason-blacklist");
    }

    public ConfigList getDropItemBlacklist(EntityType type) {
        return getList(type, "drops.item-blacklist");
    }

    public ConfigList getDropItemOnePer(EntityType type) {
        return getList(type, "drops.one-per-stack");
    }

    public boolean isExpMultiEnabled(EntityType type) {
        return getBoolean(type, "experience.enabled");
    }

    public ConfigList getExpTypeBlacklist(EntityType type) {
        return getList(type, "experience.type-blacklist");
    }

    public double getExpMinBound(EntityType type) {
        return getDouble(type, "experience.multiplier-min");
    }

    public double getExpMaxBound(EntityType type) {
        return getDouble(type, "experience.multiplier-max");
    }

    public boolean isPlayerStatMulti(EntityType type) {
        return getBoolean(type, "player-stats");
    }

    public boolean isWaitingEnabled(EntityType type) {
        return getBoolean(type, "wait-to-stack.enabled");
    }

    public ConfigList getWaitingTypes(EntityType type) {
        return getList(type, "wait-to-stack.types-whitelist");
    }

    public ConfigList getWaitingReasons(EntityType type) {
        return getList(type, "wait-to-stack.reasons-whitelist");
    }

    public int getWaitingTime(EntityType type) {
        return getInt(type, "wait-to-stack.wait-time");
    }

    public int getMaxDeathStep(EntityType type) {
        return getInt(type, "death.STEP.max-step");
    }

    public int getMinDeathStep(EntityType type) {
        return getInt(type, "death.STEP.min-step");
    }

    public boolean isListenerEnabled(String eventKey) {
        return getBoolean("events." + eventKey);
    }

    public boolean removeStackDataOnDivide(String reasonKey) { return getBoolean("events.remove-stack-data." + reasonKey); }

    public boolean isTargetingDisabled(EntityType type) {
        return getBoolean(type, "disable-targeting.enabled");
    }

    public ConfigList getTargetingDisabledTypes(EntityType type) {
        return getList(type, "disable-targeting.type-blacklist");
    }

    public ConfigList getTargetingDisabledReasons(EntityType type) {
        return getList(type, "disable-targeting.reason-blacklist");
    }

    public ListenerMode getListenerMode(EntityType type, String eventKey) {
        return ListenerMode.valueOf(getString(type, "events." + eventKey + ".mode"));
    }

    public int getEventMultiplyLimit(EntityType type, String eventKey, int stackSize) {
        int limit =  getInt(type, "events." + eventKey + ".limit");
        return limit == -1 ? stackSize : Math.min(stackSize, limit);
    }

    public boolean isWorldBlacklisted(EntityType type, World world) {
        return getList(type, "worlds-blacklist").contains(world.getName());
    }

    public boolean isEntityBlacklisted(LivingEntity entity, CreatureSpawnEvent.SpawnReason reason) {
        if (getList(entity.getType(), "types-blacklist").contains(entity.getType().toString())) {
            return true;
        }
        if (getList(entity.getType(), "reason-blacklist").contains(reason.toString())) {
            return true;
        }
        return isWorldBlacklisted(entity.getType(), entity.getWorld());
    }

    public DeathType getDeathType(LivingEntity dead) {
        for (DeathType type : getDeathSection(dead)) {
            ConfigList reasons = getList(dead.getType(), "death." + type + ".reason-blacklist");
            if (dead.getLastDamageCause() != null && reasons.contains(dead.getLastDamageCause().getCause().toString())) {
                continue;
            }
            ConfigList spawnReasons = getList(dead.getType(), "death." + type + ".spawn-reason-blacklist");
            if (Utilities.isPaper() && spawnReasons.contains(dead.getEntitySpawnReason())) {
                continue;
            }
            ConfigList types = getList(dead.getType(), "death." + type + ".type-blacklist");
            if (types.contains(dead.getType().toString())) {
                continue;
            }
            return type;
        }
        throw new UnsupportedOperationException("Configuration error - unable to determine death type!");
    }

    private Collection<DeathType> getDeathSection(LivingEntity dead) {
        TreeMap<Integer, DeathType> array = new TreeMap<>();
        for (DeathType type : DeathType.values()) {
            array.put(getInt(dead.getType(), "death." + type + ".priority"), type);
        }
        return array.values();
    }

    public boolean isSkipDeathAnimation(EntityType type) {
        return getBoolean(type, "death.skip-animation") && Utilities.isPaper();
    }

    public StackEntity.EquipItemMode getEquipItemMode(EntityType type) {
        return StackEntity.EquipItemMode.valueOf(getString(type, "events.equip.mode"));
    }

    @Override
    public void updateFile() throws IOException {
        if (isSet("check-area.x")) {
            sm.getLogger().info("Old config detected. Renaming to config.old and making a new one.");
            makeOld();
            sm.downloadBridge();
            return;
        }
        super.updateFile();
    }

}
