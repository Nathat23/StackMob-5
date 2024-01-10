package uk.antiperson.stackmob.hook.hooks;

import me.minebuilders.clearlag.events.EntityRemoveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.hook.Hook;
import uk.antiperson.stackmob.hook.HookMetadata;

@HookMetadata(name = "ClearLag", config = "clearlagg")
public class ClearlaggHook extends Hook implements Listener {

    public ClearlaggHook(StackMob sm) {
        super(sm);
    }

    @EventHandler
    public void onEntityRemove(EntityRemoveEvent event) {
        for (StackEntity stackEntity : sm.getEntityManager().getStackEntities()) {
            event.addEntity(stackEntity.getEntity());
        }
    }
}
