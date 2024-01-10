package uk.antiperson.stackmob.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import uk.antiperson.stackmob.StackMob;

public class ChunkListener implements Listener {

    private StackMob sm;
    public ChunkListener(StackMob sm) {
        this.sm = sm;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        sm.getEntityManager().registerStackedEntities(event.getChunk());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        sm.getEntityManager().unregisterStackedEntities(event.getChunk());
    }
}
