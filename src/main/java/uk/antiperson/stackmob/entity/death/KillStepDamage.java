package uk.antiperson.stackmob.entity.death;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.entity.traits.TraitMetadata;
import uk.antiperson.stackmob.entity.traits.trait.Potion;
import uk.antiperson.stackmob.hook.StackableMobHook;
import uk.antiperson.stackmob.hook.hooks.MythicMobsStackHook;

public class KillStepDamage extends DeathMethod {

    private double leftOverDamage;
    private boolean useValue;

    public KillStepDamage(StackMob sm, StackEntity dead) {
        super(sm, dead);
    }

    @Override
    public int calculateStep() {
        if (getDead().getEntity().getLastDamageCause() == null) {
            return 1;
        }
        double healthBefore = ((LivingEntity)getDead().getEntity().getLastDamageCause().getEntity()).getHealth();
        double damageDone = getEntity().getLastDamageCause().getFinalDamage();
        double maxHealth = getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue();
        if (getStackMob().getMainConfig().isTraitEnabled(Potion.class.getAnnotation(TraitMetadata.class).path())) {
            if (getDead().getEntity().getPotionEffect(PotionEffectType.HEALTH_BOOST) != null) {
                maxHealth = getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                useValue = true;
            }
        }
        double damageLeft = Math.min(maxHealth * (getDead().getSize() - 1), Math.abs(healthBefore - damageDone));
        double divided = damageLeft / maxHealth;
        int entities = (int) Math.floor(divided);
        leftOverDamage = (divided - entities) * maxHealth;
        return entities + 1;
    }

    @Override
    public void onSpawn(StackEntity spawned) {
        AttributeInstance attribute = getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH);
        AttributeInstance spawnedAttribute = spawned.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH);
        double maxHealth = useValue ? attribute.getValue() : attribute.getDefaultValue();
        StackableMobHook smh = sm.getHookManager().getApplicableHook(spawned);
        if (smh instanceof MythicMobsStackHook) {
            sm.getServer().getScheduler().runTaskLater(sm, bukkitTask -> {
                if (!spawned.getEntity().isDead()) {
                    spawned.getEntity().setHealth(maxHealth - leftOverDamage);
                }
            }, 5);
        }
        try {
            spawned.getEntity().setHealth(maxHealth - leftOverDamage);
        } catch (IllegalArgumentException e) {
            sm.getLogger().warning("New health value is too high! Please report and include the message below.");
            sm.getLogger().info(attribute.getBaseValue() + "," + attribute.getDefaultValue() + "," + attribute.getValue() + "," + leftOverDamage);
            if (spawnedAttribute != null) {
                sm.getLogger().info(spawnedAttribute.getBaseValue() + "," + spawnedAttribute.getDefaultValue() + "," + spawnedAttribute.getValue());
            }
        }
    }
}
