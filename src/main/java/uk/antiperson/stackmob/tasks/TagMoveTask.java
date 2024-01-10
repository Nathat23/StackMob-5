package uk.antiperson.stackmob.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.packets.PlayerWatcher;

import java.util.ArrayList;

public class TagMoveTask extends BukkitRunnable {

    private final StackMob sm;
    public TagMoveTask(StackMob sm) {
        this.sm = sm;
    }

    @Override
    public void run() {
        ArrayList<Player> playerArrayList = new ArrayList<>(Bukkit.getOnlinePlayers());
        sm.getServer().getScheduler().runTaskAsynchronously(sm, () -> {
            for (Player player : playerArrayList) {
                PlayerWatcher playerWatcher = sm.getPlayerManager().getPlayerWatcher(player);
                if (playerWatcher == null) {
                    continue;
                }
                playerWatcher.updateTagLocations();
            }
        });
    }
}
