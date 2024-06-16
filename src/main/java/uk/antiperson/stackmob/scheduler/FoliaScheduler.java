package uk.antiperson.stackmob.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class FoliaScheduler implements Scheduler {
    @Override
    public void runGlobalTaskTimer(Plugin plugin, Runnable runnable, long delay, long period) {
        plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> runnable.run(), (int) delay, (int) period);
    }

    @Override
    public void runTask(Plugin plugin, Location location, Runnable runnable) {
        plugin.getServer().getRegionScheduler().run(plugin, location, scheduledTask -> runnable.run());
    }

    @Override
    public void runTask(Plugin plugin, Entity entity, Runnable runnable) {
        entity.getScheduler().run(plugin, scheduledTask -> runnable.run(), () -> {});
    }

    @Override
    public void runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> runnable.run());
    }

    @Override
    public void runTaskLater(Plugin plugin, Entity entity, Runnable runnable, long delay) {
        entity.getScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), () -> {}, (int) delay);
    }
}
