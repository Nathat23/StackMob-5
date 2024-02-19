package uk.antiperson.stackmob.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.packets.PlayerWatcher;
import uk.antiperson.stackmob.utils.Utilities;

public class TagCheckTask implements Runnable {

    private final StackMob sm;
    public TagCheckTask(StackMob sm) {
        this.sm = sm;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Runnable runnable = () -> {
                PlayerWatcher playerWatcher = sm.getPlayerManager().createPlayerWatcher(player);
                playerWatcher.checkPlayer();
            };

            if (Utilities.IS_FOLIA) {
                sm.getScheduler().runTask(sm, player, runnable);
            } else {
                runnable.run();
            }
        }
    }



}
