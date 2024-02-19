package uk.antiperson.stackmob.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.packets.PlayerWatcher;
import uk.antiperson.stackmob.utils.Utilities;

import java.util.ArrayList;

public class TagMoveTask extends BukkitRunnable {

    private final StackMob sm;
    public TagMoveTask(StackMob sm) {
        this.sm = sm;
    }

    @Override
    public void run() {
        ArrayList<Player> playerArrayList = new ArrayList<>(Bukkit.getOnlinePlayers());
        sm.getScheduler().runTaskAsynchronously(sm, () -> {
            for (Player player : playerArrayList) {
                PlayerWatcher playerWatcher = sm.getPlayerManager().getPlayerWatcher(player);
                if (playerWatcher == null) {
                    continue;
                }

                Runnable runnable = playerWatcher::updateTagLocations;
                if (Utilities.IS_FOLIA) {
                    sm.getScheduler().runTask(sm, player, runnable);
                } else {
                    runnable.run();
                }
            }
        });
    }
}
