package uk.antiperson.stackmob.commands.subcommands;

import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.commands.ArgumentType;
import uk.antiperson.stackmob.commands.CommandArgument;
import uk.antiperson.stackmob.commands.CommandMetadata;
import uk.antiperson.stackmob.commands.SubCommand;
import uk.antiperson.stackmob.commands.User;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.packets.PlayerWatcher;
import uk.antiperson.stackmob.packets.TagHandler;

import java.util.Arrays;

@CommandMetadata(command = "stats", playerReq = false, desc = "View plugin statistics.")
public class Stats extends SubCommand {

    private final StackMob sm;
    public Stats(StackMob sm) {
        super(CommandArgument.construct(ArgumentType.STRING, false, Arrays.asList("mobs", "players")));
        this.sm = sm;
    }

    @Override
    public boolean onCommand(User sender, String[] args) {
        switch (args[0]) {
            case "mobs":
                sendMobStats(sender);
                break;
            case "players":
                sendPlayerStats(sender);
                break;
        }
        return false;
    }

    private void sendMobStats(User sender) {
        int total = 0;
        int waiting = 0;
        int full = 0;
        for (StackEntity stackEntity : sm.getEntityManager().getStackEntities()) {
            if (stackEntity.isWaiting()) {
                waiting += 1;
            }
            if (stackEntity.isMaxSize()) {
                full += 1;
            }
            total += stackEntity.getSize();
        }
        sender.sendInfo("Stacking statistics:");
        sender.sendRawMessage("Total stack entities: " + sm.getEntityManager().getStackEntities().size() + " (" + total + " single entities.)");
        sender.sendRawMessage("Full stacks: " + full + " Waiting to stack: " + waiting);
    }

    private void sendPlayerStats(User sender) {
        int trackingOverall = 0;
        int trackingStacks = 0;
        int visible = 0;
        for (PlayerWatcher playerWatcher : sm.getPlayerManager().geWatchers()) {
            trackingOverall += 1;
            if (!playerWatcher.getPlayer().equals(sender.getSender())) {
                continue;
            }
            for (TagHandler tagHandler : playerWatcher.getTagHandlers()) {
                trackingStacks += 1;
                if (tagHandler.isTagVisible()) {
                    visible += 1;
                }
            }
        }
        int notVisible = trackingStacks - visible;
        sender.sendInfo("Player statistics:");
        sender.sendRawMessage("We are tracking " + trackingOverall + " players.");
        sender.sendRawMessage("You have " + trackingStacks + " stacks in range. " + visible + " should have visible tags.");
        sender.sendRawMessage(notVisible + " should have hidden tags.");
    }
}
