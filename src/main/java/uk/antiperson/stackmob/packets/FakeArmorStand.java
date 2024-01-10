package uk.antiperson.stackmob.packets;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import uk.antiperson.stackmob.utils.Utilities;

public interface FakeArmorStand {

    void spawnFakeArmorStand(Entity owner, Location location, Component name, double offset);

    void updateName(Component newName);

    void teleport(Entity entity, double offset);

    void removeFakeArmorStand();

    default Location adjustLocation(Entity entity, double offset) {
        double adjustment = shouldAdjust(entity) ? 0.3 : 0.1;
        if (offset > 0) {
            adjustment = offset;
        }
        return entity.getLocation().add(0, entity.getHeight() + adjustment, 0);
    }

    private boolean shouldAdjust(Entity entity) {
        String name = entity.getCustomName();
        return name != null && name.length() != 0;
    }


}
