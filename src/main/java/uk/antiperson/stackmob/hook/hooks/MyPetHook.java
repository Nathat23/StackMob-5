package uk.antiperson.stackmob.hook.hooks;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.entity.MyPet;
import de.Keyle.MyPet.api.entity.MyPetBukkitEntity;
import org.bukkit.entity.LivingEntity;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.hook.Hook;
import uk.antiperson.stackmob.hook.HookMetadata;
import uk.antiperson.stackmob.hook.PreventStackHook;

import java.util.Optional;

@HookMetadata(name = "MyPet", config = "mypet")
public class MyPetHook extends Hook implements PreventStackHook {

    public MyPetHook(StackMob sm) {
        super(sm);
    }

    @Override
    public boolean isCustomMob(LivingEntity entity) {
        for (MyPet myPet : MyPetApi.getMyPetManager().getAllActiveMyPets()) {
            Optional<MyPetBukkitEntity> optional = myPet.getEntity();
            if (!optional.isPresent()) {
                continue;
            }
            if (optional.get().getUniqueId().equals(entity.getUniqueId())) {
                return true;
            }
        }
        return false;
    }
}
