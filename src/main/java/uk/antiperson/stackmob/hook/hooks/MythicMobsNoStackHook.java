package uk.antiperson.stackmob.hook.hooks;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.entity.LivingEntity;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.hook.Hook;
import uk.antiperson.stackmob.hook.HookMetadata;
import uk.antiperson.stackmob.hook.PreventStackHook;

@HookMetadata(name = "MythicMobs", config = "mythicmobs.prevent-stack")
public class MythicMobsNoStackHook extends Hook implements PreventStackHook {

    private MythicBukkit mythicMobs;
    public MythicMobsNoStackHook(StackMob sm) {
        super(sm);
    }

    @Override
    public boolean isCustomMob(LivingEntity entity) {
        return mythicMobs.getMobManager().isActiveMob(entity.getUniqueId());
    }

    @Override
    public void onEnable() {
        mythicMobs = (MythicBukkit) getPlugin();
    }
}
