package uk.antiperson.stackmob.commands.subcommands;

import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.commands.CommandMetadata;
import uk.antiperson.stackmob.commands.SubCommand;
import uk.antiperson.stackmob.commands.User;

import java.io.IOException;

@CommandMetadata(command = "reload", playerReq = false, desc = "Reloads the config files.")
public class Reload extends SubCommand {

    private final StackMob sm;
    public Reload(StackMob sm) {
        this.sm = sm;
    }

    @Override
    public boolean onCommand(User sender, String[] args) {
        try {
            sm.getEntityTranslation().reloadConfig();
            sm.getMainConfig().reload();
            sender.sendSuccess("Reloaded config files successfully!");
            sender.sendInfo("Note: Some config changes may require a full server restart to take effect.");
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendError("An error occurred while attempting to reload the config files.");
            sender.sendInfo("Check console for more information.");
        }
        return false;
    }
}
