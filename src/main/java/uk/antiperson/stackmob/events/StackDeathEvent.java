package uk.antiperson.stackmob.events;

import org.bukkit.event.HandlerList;
import uk.antiperson.stackmob.entity.StackEntity;

/**
 * Event called when a stack dies.
 */
public class StackDeathEvent extends StackEvent {

    private static final HandlerList handlers = new HandlerList();
    private int deathStep;

    public StackDeathEvent(StackEntity stackEntity, int deathStep) {
        super(stackEntity);
        this.deathStep = deathStep;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the amount of entities removed from the stack when it was killed.
     * @return the amount of entities removed from the stack when it was killed.
     */
    public int getDeathStep() {
        return deathStep;
    }

    /**
     * Set the amount of entities to be removed from the stack.
     */
    public void setDeathStep(int deathStep) {
        if (deathStep < 0) {
            throw new IllegalArgumentException("Death step must be greater than or equal to one.");
        }
        if (deathStep > getStackEntity().getSize()) {
            throw new IllegalArgumentException("Death step cannot be greater than the size of the killed entity!");
        }
        this.deathStep = deathStep;
    }
}
