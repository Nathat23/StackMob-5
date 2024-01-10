package uk.antiperson.stackmob.commands.subcommands;

import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.commands.CommandMetadata;
import uk.antiperson.stackmob.commands.SubCommand;
import uk.antiperson.stackmob.commands.User;

@CommandMetadata(command = "upgrade", playerReq = false, desc = "Updates StackMob to the latest version.")
public class Upgrade extends SubCommand {

    private final StackMob sm;
    public Upgrade(StackMob sm) {
        this.sm = sm;
    }

    @Override
    public boolean onCommand(User sender, String[] args) {
        sender.sendInfo("Starting download. Please wait...");
        sm.getUpdater().downloadUpdate().whenComplete((downloadResult, throwable) -> {
            switch (downloadResult) {
                case ERROR:
                    sender.sendError("An error occurred while downloading the update.");
                    break;
                case SUCCESSFUL:
                    sender.sendSuccess("The new update was downloaded successfully!");
                    sender.sendInfo("To apply this update, make sure to restart your server.");
                    break;
            }
        });
        return false;
    }
}
