package uk.antiperson.stackmob.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public interface Scheduler {
    void runGlobalTaskTimer(Plugin plugin, Runnable runnable, long delay, long period);
    void runTask(Plugin plugin, Location location, Runnable runnable);
    void runTask(Plugin plugin, Entity entity, Runnable runnable);
    void runTaskAsynchronously(Plugin plugin, Runnable runnable);
    void runTaskLater(Plugin plugin, Entity entity, Runnable runnable, long delay);
}
