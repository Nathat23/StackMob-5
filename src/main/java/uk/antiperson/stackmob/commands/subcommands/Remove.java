package uk.antiperson.stackmob.commands.subcommands;

import org.bukkit.entity.*;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.commands.*;
import uk.antiperson.stackmob.entity.StackEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@CommandMetadata(command = "remove", playerReq = true, desc = "Remove all entities")
public class Remove extends SubCommand {

    private final StackMob sm;
    public Remove(StackMob sm) {
        super(CommandArgument.construct(ArgumentType.STRING, true, Arrays.asList("animals", "hostile")));
        this.sm = sm;
    }

    @Override
    public boolean onCommand(User sender, String[] args) {
        Function<Entity, Boolean> function = entity -> entity instanceof Mob;
        if (args.length == 1) {
            switch (args[0]) {
                case "animals":
                    function = entity -> entity instanceof Animals;
                    break;
                case "hostile":
                    function = entity -> entity instanceof Monster;
                    break;
            }
        }
        Set<StackEntity> toRemove = new HashSet<>();
        for (StackEntity stackEntity : sm.getEntityManager().getStackEntities()) {
            if (!function.apply(stackEntity.getEntity())) {
                continue;
            }
            stackEntity.remove(false);
            toRemove.add(stackEntity);
        }
        toRemove.forEach(stackEntity -> sm.getEntityManager().unregisterStackedEntity(stackEntity));
        sender.sendSuccess("Entities matching your criteria have been removed.");
        return false;
    }

}
