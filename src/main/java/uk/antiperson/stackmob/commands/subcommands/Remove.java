package uk.antiperson.stackmob.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.commands.*;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.utils.Utilities;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@CommandMetadata(command = "remove", playerReq = false, desc = "Remove entities.")
public class Remove extends SubCommand {

    private final StackMob sm;
    public Remove(StackMob sm) {
        super(CommandArgument.construct(ArgumentType.STRING, false, Arrays.asList("chunk", "world", "all")),
                CommandArgument.construct(ArgumentType.STRING, true, Arrays.asList("animals", "hostile")));
        this.sm = sm;
    }

    @Override
    public boolean onCommand(User sender, String[] args) {
        Function<Entity, Boolean> function = entity -> entity instanceof Mob;
        if (args.length == 2) {
            switch (args[1]) {
                case "animals":
                    function = entity -> entity instanceof Animals;
                    break;
                case "hostile":
                    function = entity -> entity instanceof Monster;
                    break;
            }
        }
        Set<Chunk> chunks = new HashSet<>();
        switch (args[0]) {
            case "chunk":
                if (!(sender.getSender() instanceof Player)) {
                    sender.sendError("You need to be a player!");
                    return false;
                }
                chunks.add(((Player) sender.getSender()).getLocation().getChunk());
                break;
            case "world":
                if (!(sender.getSender() instanceof Player)) {
                    sender.sendError("You need to be a player!");
                    return false;
                }
                chunks.addAll(List.of(((Player) sender.getSender()).getWorld().getLoadedChunks()));
                break;
            case "all":
                Bukkit.getWorlds().forEach(world -> chunks.addAll(List.of(world.getLoadedChunks())));
                break;
        }
        for (Chunk chunk : chunks) {
            for (Entity entity : chunk.getEntities()) {
                if (!function.apply(entity)) {
                    continue;
                }
                StackEntity stackEntity = sm.getEntityManager().getStackEntity((LivingEntity) entity);
                if (stackEntity == null) {
                    continue;
                }
                stackEntity.remove();
            }
        }
        sender.sendSuccess(Utilities.capitalizeString(args[0]) + " entities matching your criteria have been removed.");
        return false;
    }

}
