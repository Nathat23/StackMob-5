package uk.antiperson.stackmob.listeners;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockShearEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootContext;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.config.EntityConfig;
import uk.antiperson.stackmob.entity.Drops;
import uk.antiperson.stackmob.entity.StackEntity;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

@ListenerMetadata(config = "events.shear.enabled")
public class ShearListener implements Listener {

    private final StackMob sm;

    public ShearListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler
    public void onShearSheep(PlayerShearEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        EquipmentSlot equipmentSlot = findShears(event.getPlayer());
        if (equipmentSlot == null) {
            sm.getLogger().info("A player just managed to shear an entity while not holding shears.");
            return;
        }
        ItemStack is = shearLogic((LivingEntity) event.getEntity(), event.getPlayer().getInventory().getItem(equipmentSlot));
        if (is == null) {
            return;
        }
        event.getPlayer().getInventory().setItem(equipmentSlot, is);
    }

    @EventHandler
    public void onShearSheep(BlockShearEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        ItemStack is = shearLogic((LivingEntity) event.getEntity(), event.getTool());
        int durability = ((Damageable) event.getTool().getItemMeta()).getDamage();
        int maxDurability = event.getTool().getType().getMaxDurability();
        if (is == null || (maxDurability - durability) == 1) {
            return;
        }
        sm.getScheduler().runTask(sm, event.getBlock().getLocation(), () -> {
            Dispenser dispenser = (Dispenser) event.getBlock().getState();
            dispenser.getInventory().setItem(dispenser.getInventory().first(event.getTool()), is);
        });
    }

    private EquipmentSlot findShears(Player player) {
        EquipmentSlot hand = checkSlot(player, EquipmentSlot.HAND);
        EquipmentSlot offHand = checkSlot(player, EquipmentSlot.OFF_HAND);
        return hand == null ? offHand : hand;
    }

    private EquipmentSlot checkSlot(Player player, EquipmentSlot slot) {
        if (player.getInventory().getItem(slot).getType() == Material.SHEARS) {
            return slot;
        }
        return null;
    }

    private ItemStack shearLogic(LivingEntity entity, ItemStack item) {
        if (!((entity instanceof Sheep) || (entity instanceof MushroomCow))) {
            return null;
        }
        StackEntity stackEntity = sm.getEntityManager().getStackEntity(entity);
        if (stackEntity == null || stackEntity.isSingle()) {
            return null;
        }
        EntityConfig.ListenerMode shear = stackEntity.getEntityConfig().getListenerMode(EntityConfig.EventType.SHEAR);
        if (shear == EntityConfig.ListenerMode.SPLIT) {
            StackEntity slice = stackEntity.slice();
            if (slice.getEntity() instanceof Sheep) {
                ((Sheep) slice.getEntity()).setSheared(false);
            }
            return null;
        }
        int limit = stackEntity.getEntityConfig().getEventMultiplyLimit(EntityConfig.EventType.SHEAR, stackEntity.getSize());
        Damageable damageable = (Damageable) item.getItemMeta();
        int health = item.getType().getMaxDurability() - damageable.getDamage();
        int amount = Math.min(health, limit);
        stackEntity.splitIfNotEnough(amount);
        int damage = health - amount;
        if (damage > 0) {
            damageable.setDamage(damageable.getDamage() + amount);
            item.setItemMeta((ItemMeta) damageable);
        } else {
            item = new ItemStack(Material.AIR);
        }
        if (entity instanceof Sheep) {
            Sheep sheared = (Sheep) entity;
            LootContext lootContext = new LootContext.Builder(sheared.getLocation()).lootedEntity(sheared).build();
            Collection<ItemStack> loot = sheared.getLootTable().populateLoot(ThreadLocalRandom.current(), lootContext);
            for (ItemStack itemStack : loot) {
                if (Tag.WOOL.isTagged(itemStack.getType())) {
                    int woolAmount = (int) Math.round(amount * ThreadLocalRandom.current().nextDouble(1, 2));
                    Drops.dropItem(sheared.getLocation(), itemStack, woolAmount, true);
                }
            }
            return item;
        }
        MushroomCow mushroomCow = (MushroomCow) entity;
        ItemStack mushrooms = new ItemStack(getMaterial(mushroomCow), 1);
        Drops.dropItem(mushroomCow.getLocation(), mushrooms, (amount - 1) * 5, true);
        // Spawn separate normal cow for the rest of the stack.
        Entity cow = mushroomCow.getWorld().spawnEntity(mushroomCow.getLocation(), EntityType.COW);
        StackEntity stackCow = sm.getEntityManager().registerStackedEntity((LivingEntity) cow);
        stackCow.setSize(amount - 1);
        return item;
    }

    private Material getMaterial(MushroomCow mushroomCow) {
        return mushroomCow.getVariant() == MushroomCow.Variant.RED ? Material.RED_MUSHROOM : Material.BROWN_MUSHROOM;
    }
}
