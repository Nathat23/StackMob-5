package uk.antiperson.stackmob.packets;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.hook.hooks.ProtocolLibHook;

public class ProtocolLibFakeArmorStand implements FakeArmorStand {

    private final StackMob sm;
    private ProtocolLibHook plh;
    private int entityId;
    private final Player player;
    public ProtocolLibFakeArmorStand(StackMob sm, Player player) {
        this.sm = sm;
        this.player = player;
    }

    @Override
    public void spawnFakeArmorStand(Entity owner, Location location, Component name, double offset) {
        if (plh == null) {
            plh = sm.getHookManager().getProtocolLibHook();
        }
        entityId = plh.spawnFakeArmorStand(player, adjustLocation(owner, offset), name);
    }

    @Override
    public void updateName(Component newName) {
        if (plh == null) {
            plh = sm.getHookManager().getProtocolLibHook();
        }
        plh.updateTag(player, entityId, newName);
    }

    @Override
    public void teleport(Entity entity, double offset) {
        if (plh == null) {
            plh = sm.getHookManager().getProtocolLibHook();
        }
        plh.teleport(player, entityId, adjustLocation(entity, offset));
    }

    @Override
    public void removeFakeArmorStand() {
        if (plh == null) {
            plh = sm.getHookManager().getProtocolLibHook();
        }
        plh.removeFakeArmorStand(player, entityId);
    }
}
