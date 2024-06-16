package uk.antiperson.stackmob.utils;

import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class Updater {

    private final int resourceId;
    private final Plugin sm;
    public Updater(Plugin sm, int resourceId) {
        this.sm = sm;
        this.resourceId = resourceId;
    }

    public CompletableFuture<UpdateResult> checkUpdate() {
        return CompletableFuture.supplyAsync(() -> {
           String latestVersion = getLatestVersion();
           if (latestVersion == null) {
               return new UpdateResult(VersionResult.ERROR);
           }
           String strippedLatest = latestVersion.replaceAll("[^A-Za-z0-9]", "");
           String strippedCurrent = sm.getDescription().getVersion().replaceAll("[^A-Za-z0-9]", "");
           if (strippedCurrent.equals(strippedLatest)) {
               return new UpdateResult(VersionResult.NONE);
           }
           return new UpdateResult(VersionResult.AVAILABLE, latestVersion);
        });
    }

    private String getLatestVersion(){
        try{
            URL updateUrl = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
            HttpURLConnection connect = (HttpURLConnection) updateUrl.openConnection();
            connect.setRequestMethod("GET");
            return new BufferedReader(new InputStreamReader(connect.getInputStream())).readLine();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public CompletableFuture<Utilities.DownloadResult> downloadUpdate() {
        File currentFile = new File(sm.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        File updateFile = new File(sm.getServer().getUpdateFolderFile(), currentFile.getName());
        return Utilities.downloadFile(updateFile, "https://api.spiget.org/v2/resources/" + resourceId + "/download");
    }

    public enum VersionResult {
        AVAILABLE,
        NONE,
        ERROR
    }

    public class UpdateResult {

        private final VersionResult result;
        private String newVersion;
        UpdateResult(VersionResult result, String newVersion) {
            this.result = result;
            this.newVersion = newVersion;
        }

        UpdateResult(VersionResult result) {
            this.result = result;
        }

        public VersionResult getResult() {
            return result;
        }

        public String getNewVersion() {
            return newVersion;
        }
    }
}
