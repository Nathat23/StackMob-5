package uk.antiperson.stackmob.config;

import uk.antiperson.stackmob.StackMob;

import java.io.IOException;

public class MainConfigFile extends ConfigFile {

    private StackMob sm;

    public MainConfigFile(StackMob sm) {
        super(sm, "config.yml");
        this.sm = sm;
    }

    @Override
    public void updateFile() throws IOException {
        if (isSet("check-area.x")) {
            sm.getLogger().info("Old config detected. Renaming to config.old and making a new one.");
            sm.getLogger().warning("You are going to loose mob stack data!!!");
            sm.getLogger().info("I would suggest either running an older version of StackMob and use the StackMobBridge plugin to covert starts. Or just replace stacks manually.");
            makeOld();
            return;
        }
        super.updateFile();
    }

    @Override
    public void reloadConfig() throws IOException {
        load();
    }
}
