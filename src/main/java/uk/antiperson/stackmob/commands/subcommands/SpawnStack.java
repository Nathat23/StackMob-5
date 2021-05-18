package uk.antiperson.stackmob.commands.subcommands;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.commands.*;
import uk.antiperson.stackmob.entity.StackEntity;

@CommandMetadata(command = "spawn", playerReq = true, desc = "Spawn a new stack.")
public class SpawnStack extends SubCommand {

    private final StackMob sm;
    public SpawnStack(StackMob sm) {
        super(CommandArgument.construct(ArgumentType.ENTITY_TYPE, false), CommandArgument.construct(ArgumentType.INTEGER, false, "stack size"));
        this.sm = sm;
    }

    @Override
    public boolean onCommand(User sender, String[] args) {
        Player player = (Player) sender.getSender();
        EntityType entityType = EntityType.valueOf(args[0].toUpperCase());
        int newSize = Integer.parseInt(args[1]);
        if (newSize > sm.getMainConfig().getMaxStack(entityType)) {
            sender.sendError("New stack value is too large! (max = " + sm.getMainConfig().getMaxStack(entityType) + ")");
            return false;
        }
        LivingEntity entity = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), entityType);
        StackEntity stackEntity = sm.getEntityManager().registerStackedEntity(entity);
        stackEntity.setSize(newSize);
        sender.sendSuccess("A new stack has been spawned.");
        return false;
    }
}
