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
            makeOld();
            sm.downloadBridge();
            return;
        }
        super.updateFile();
    }

    @Override
    public void reloadConfig() throws IOException {
        load();
    }
}
