package uk.antiperson.stackmob.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class BukkitScheduler implements Scheduler {
    @Override
    public void runGlobalTaskTimer(Plugin plugin, Runnable runnable, long delay, long period) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }

    @Override
    public void runTask(Plugin plugin, Location location, Runnable runnable) {
        plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    @Override
    public void runTask(Plugin plugin, Entity entity, Runnable runnable) {
        plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    @Override
    public void runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public void runTaskLater(Plugin plugin, Entity entity, Runnable runnable, long delay) {
        plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
    }
}
