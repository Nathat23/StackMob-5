package uk.antiperson.stackmob.hook.hooks;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.config.ConfigList;
import uk.antiperson.stackmob.hook.Hook;
import uk.antiperson.stackmob.hook.HookMetadata;
import uk.antiperson.stackmob.hook.SegregatedMobHook;
import uk.antiperson.stackmob.hook.StackableMobHook;

@HookMetadata(name = "MythicMobs", config = "mythicmobs.stack")
public class MythicMobsStackHook extends Hook implements StackableMobHook {

    private MythicMobs mythicMobs;
    public MythicMobsStackHook(StackMob sm) {
        super(sm);
    }

    @Override
    public boolean isMatching(LivingEntity first, LivingEntity nearby) {
        ActiveMob activeMobO = mythicMobs.getMobManager().getMythicMobInstance(first);
        ActiveMob activeMobN = mythicMobs.getMobManager().getMythicMobInstance(nearby);
        if(!(activeMobO.getType().equals(activeMobN.getType()))){
            return false;
        }
        ConfigList list = sm.getMainConfig().getList(first.getType(), "hooks.mythicmobs.blacklist");
        return !list.contains(activeMobN.getType().getInternalName());
    }

    @Override
    public LivingEntity spawnClone(Location location, LivingEntity dead) {
        ActiveMob activeMob = mythicMobs.getMobManager().getMythicMobInstance(dead);
        ActiveMob clone = mythicMobs.getMobManager().spawnMob(activeMob.getType().getInternalName(), location);
        if(clone != null){
            return (LivingEntity) clone.getEntity().getBukkitEntity();
        }
        return null;
    }

    @Override
    public String getDisplayName(LivingEntity entity) {
        ActiveMob activeMob = mythicMobs.getMobManager().getMythicMobInstance(entity);
        if (activeMob.getType().getConfig().getFile().getName().equals("VanillaMobs.yml")) {
            return null;
        }
        return activeMob.getDisplayName() != null ? activeMob.getDisplayName() : "MythicMob";
    }

    @Override
    public boolean isCustomMob(LivingEntity entity) {
        return mythicMobs.getMobManager().isActiveMob(entity.getUniqueId());
    }

    @Override
    public void onEnable() {
        mythicMobs = (MythicMobs) getPlugin();
    }
}
