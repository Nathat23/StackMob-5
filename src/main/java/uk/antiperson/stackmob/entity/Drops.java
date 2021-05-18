package uk.antiperson.stackmob.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.config.ConfigList;
import uk.antiperson.stackmob.utils.Utilities;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Drops {

    private final LivingEntity dead;
    private final StackMob sm;
    public Drops(StackMob sm, StackEntity entity) {
        this.sm = sm;
        this.dead = entity.getEntity();
    }

    public Map<ItemStack, Integer> calculateDrops(int deathAmount, List<ItemStack> originalDrops) {
        Map<ItemStack, Integer> items = new HashMap<>();
        if (!sm.getMainConfig().isDropMultiEnabled(dead.getType())) {
            return items;
        }
        if (sm.getMainConfig().getDropTypeBlacklist(dead.getType()).contains(dead.getType().toString())) {
            return items;
        }
        EntityDamageEvent lastDamageCause = dead.getLastDamageCause();
        if (lastDamageCause == null || sm.getMainConfig().getDropReasonBlacklist(dead.getType()).contains(lastDamageCause.getCause().toString())) {
            return items;
        }
        boolean useLootTables = sm.getMainConfig().isDropLootTables(dead.getType());
        ConfigList itemBlacklist = sm.getMainConfig().getDropItemBlacklist(dead.getType());
        ConfigList dropOneItemPer = sm.getMainConfig().getDropItemOnePer(dead.getType());
        LootContext lc = new LootContext.Builder(dead.getLocation()).lootedEntity(dead).killer(dead.getKiller()).build();
        Collection<ItemStack> genItems = originalDrops;
        for (int i = 0; i < deathAmount; i++) {
            if (useLootTables) {
                genItems = ((Mob) dead).getLootTable().populateLoot(ThreadLocalRandom.current(), lc);
            }
            for (ItemStack is : genItems) {
                if (is == null || is.getType() == Material.AIR) {
                    continue;
                }
                if (dropIsArmor(is)) {
                    continue;
                }
                if (is.getType() == Material.LEAD && dead.isLeashed()) {
                    continue;
                }
                if (itemBlacklist.contains(is.getType().toString())) {
                    continue;
                }
                int dropAmount = dropOneItemPer.contains(is.getType().toString()) ? 1 : is.getAmount();
                is.setAmount(1);
                items.compute(is, (key, amount) -> amount == null ? dropAmount : amount + dropAmount);
            }
        }
        return items;
    }

    public static void dropItems(Location location, Map<ItemStack, Integer> items) {
        items.forEach((stack, amount) -> dropItem(location, stack, amount));
    }

    public static void dropItem(Location location, ItemStack stack, int amount) {
        dropItem(location, stack, amount, false);
    }

    public static void dropItem(Location location, ItemStack stack, int amount, boolean above) {
        Location dropLocation = above ? location.add(0,1,0) : location;
        for (int itemAmount : Utilities.split(amount, stack.getMaxStackSize())) {
            stack.setAmount(itemAmount);
            location.getWorld().dropItem(dropLocation, stack);
        }
    }

    private boolean dropIsArmor(ItemStack stack) {
        if (dead.getEquipment().getItemInMainHand().equals(stack) || dead.getEquipment().getItemInOffHand().equals(stack)) {
            return true;
        }
        for (ItemStack itemStack : dead.getEquipment().getArmorContents()) {
            if (itemStack.equals(stack)) {
                return true;
            }
        }
        return false;
    }

    public int calculateDeathExperience(int deadCount, int exp) {
        if (!sm.getMainConfig().isExpMultiEnabled(dead.getType())) {
            return exp;
        }
        if (sm.getMainConfig().getExpTypeBlacklist(dead.getType()).contains(dead.getType())) {
            return exp;
        }
        double minMulti = sm.getMainConfig().getExpMinBound(dead.getType()) * exp;
        double maxMulti = sm.getMainConfig().getExpMaxBound(dead.getType()) * exp;
        return exp + calculateExperience(minMulti, maxMulti, deadCount);
    }

    private int calculateExperience(double min, double max, int entities) {
        double randRange = min == max ? max : ThreadLocalRandom.current().nextDouble(min, max);
        double randMultiplier = ThreadLocalRandom.current().nextDouble(0.5, 1);
        return (int) Math.round(randRange * randMultiplier * entities);
    }

    public void dropExperience(Location location, int min, int max, int entities) {
        ExperienceOrb exp = (ExperienceOrb) location.getWorld().spawnEntity(location, EntityType.EXPERIENCE_ORB);
        exp.setExperience(calculateExperience(min, max, entities));
    }


}
