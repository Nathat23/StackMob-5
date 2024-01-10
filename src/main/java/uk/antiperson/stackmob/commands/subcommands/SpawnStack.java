package uk.antiperson.stackmob.commands.subcommands;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.commands.*;
import uk.antiperson.stackmob.entity.StackEntity;

import java.util.HashSet;
import java.util.UUID;

@CommandMetadata(command = "spawn", playerReq = true, desc = "Spawn a new stack.")
public class SpawnStack extends SubCommand {

    private final StackMob sm;
    private final HashSet<UUID> confirm;
    public SpawnStack(StackMob sm) {
        super(CommandArgument.construct(ArgumentType.ENTITY_TYPE, false),
                CommandArgument.construct(ArgumentType.INTEGER, false, "stack size"),
                CommandArgument.construct(ArgumentType.INTEGER, true, "number of stacks"));
        this.sm = sm;
        this.confirm = new HashSet<>();
    }

    @Override
    public boolean onCommand(User sender, String[] args) {
        Player player = (Player) sender.getSender();
        EntityType entityType = EntityType.valueOf(args[0].toUpperCase());
        int newSize = Integer.parseInt(args[1]);
        if (newSize < 1) {
            sender.sendError("You cannot spawn a stack with size less than one!");
            return false;
        }
        int maxSize = sm.getMainConfig().getConfig(entityType).getMaxStack();
        if (newSize > maxSize) {
            sender.sendError("Provided stack value is too large! (the maximum for " + entityType + " is " + maxSize + ")");
            return false;
        }
        int amountOfStacks = args.length > 2 ? Integer.parseInt(args[2]) : 1;
        if (amountOfStacks < 1) {
            sender.sendError("You cannot spawn less than one stack!");
            return false;
        }
        if (amountOfStacks > 20 && !confirm.contains(((Player) sender.getSender()).getUniqueId())) {
            sender.sendInfo("Are you sure you want to spawn " + amountOfStacks + " stacks?");
            sender.sendInfo("Run the same command again to confirm.");
            confirm.add(((Player) sender.getSender()).getUniqueId());
            return false;
        }
        for (int i = 0; i < amountOfStacks; i++) {
            LivingEntity entity = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), entityType);
            StackEntity stackEntity = sm.getEntityManager().registerStackedEntity(entity);
            stackEntity.setSize(newSize);
        }
        String stackString = amountOfStacks == 1 ? "A new stack has" : amountOfStacks + " stacks have";
        sender.sendSuccess(stackString + " been spawned.");
        confirm.remove(((Player) sender.getSender()).getUniqueId());
        return false;
    }
}
