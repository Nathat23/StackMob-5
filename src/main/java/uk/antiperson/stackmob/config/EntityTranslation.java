package uk.antiperson.stackmob.config;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import uk.antiperson.stackmob.StackMob;

import java.io.IOException;

public class EntityTranslation extends ConfigFile {

    public EntityTranslation(StackMob sm) {
        super(sm, "entity-translation.yml");
    }

    public String getTranslatedName(EntityType type) {
        return getString(type.toString());
    }

    @Override
    public void createFile() throws IOException {
        super.createFile();
        for (EntityType type : EntityType.values()) {
            if (type.getEntityClass() == null) {
                continue;
            }
            if (Mob.class.isAssignableFrom(type.getEntityClass())) {
                set(type.toString(), "");
            }
        }
        save();
    }

    @Override
    public void reloadConfig() throws IOException {
        load();
    }
}
