package uk.antiperson.stackmob.hook.hooks;

import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.entity.LivingEntity;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.hook.Hook;
import uk.antiperson.stackmob.hook.HookMetadata;
import uk.antiperson.stackmob.hook.SegregatedMobHook;

@HookMetadata(name = "MythicMobs", config = "mythicmobs.prevent-stack")
public class MythicMobsNoStackHook extends Hook implements SegregatedMobHook {

    private MythicMobs mythicMobs;
    public MythicMobsNoStackHook(StackMob sm) {
        super(sm);
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
