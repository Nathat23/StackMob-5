package uk.antiperson.stackmob.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.packets.PlayerWatcher;

public class TagCheckTask extends BukkitRunnable {

    private final StackMob sm;
    public TagCheckTask(StackMob sm) {
        this.sm = sm;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerWatcher playerWatcher = sm.getPlayerManager().createPlayerWatcher(player);
            playerWatcher.checkPlayer();
        }
    }



}
