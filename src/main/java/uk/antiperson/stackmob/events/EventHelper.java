package uk.antiperson.stackmob.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import uk.antiperson.stackmob.entity.StackEntity;

public class EventHelper {

    public static StackMergeEvent callStackMergeEvent(StackEntity first, StackEntity nearby) {
        StackMergeEvent event = new StackMergeEvent(first, nearby);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static StackDeathEvent callStackDeathEvent(StackEntity dead, int deathStep) {
        StackDeathEvent event = new StackDeathEvent(dead, deathStep);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static StackSpawnEvent callStackSpawnEvent(LivingEntity spawned) {
        StackSpawnEvent event = new StackSpawnEvent(spawned);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }
}
