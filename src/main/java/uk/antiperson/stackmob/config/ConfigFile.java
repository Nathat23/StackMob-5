package uk.antiperson.stackmob.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.utils.Utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;

public abstract class ConfigFile implements Config {

    private File file;
    private FileConfiguration fileCon;
    private final StackMob sm;
    private final String filePath;
    public ConfigFile(StackMob sm, String filePath) {
        this.sm = sm;
        this.filePath = filePath;
    }

    public String getString(String path) {
        return fileCon.getString(path);
    }

    public String getString(String path, String defaultString) {
        return fileCon.getString(path, defaultString);
    }

    public int getInt(String path) {
        return fileCon.getInt(path);
    }

    public double getDouble(String path) {
        return fileCon.getDouble(path);
    }

    public boolean getBoolean(String path) {
        return fileCon.getBoolean(path);
    }

    public ConfigList getList(String path) {
        List<?> list = fileCon.getList(path);
        if (list == null) {
            throw new UnsupportedOperationException(path + " list is null!");
        }
        boolean inverted = getBoolean(path + "-invert");
        return new ConfigList(this, list, path, inverted);
    }

    public ConfigurationSection getConfigurationSection(String path) {
        return fileCon.getConfigurationSection(path);
    }

    public boolean isSet(String path) {
        return fileCon.isSet(path);
    }

    public boolean isString(String path) {
        return fileCon.isString(path);
    }

    public Object get(String path) {
        return fileCon.get(path);
    }

    public List<Integer> getIntegerList(String path) {
        return fileCon.getIntegerList(path);
    }

    public boolean isFileLoaded() {
        return fileCon != null;
    }

    public void set(String path, Object value) {
        fileCon.set(path, value);
    }

    public Set<String> getKeys(boolean deep) {
        return fileCon.getKeys(deep);
    }

    /**
     * Loads the config so that it can be read.
     * @throws IOException when an I/O error occurs if a new file is made.
     */
    public void load() throws IOException {
        file = new File(sm.getDataFolder(), filePath);
        if (!file.exists()) {
            createFile();
        }
        fileCon = YamlConfiguration.loadConfiguration(file);
        updateFile();
    }

    /**
     * Rename the current file and create a new one from defaults.
     * @throws IOException when an I/O error occurs reading or writing.
     */
    public void makeOld() throws IOException {
        File oldFile = new File(sm.getDataFolder(), filePath.replace(".yml", ".old"));
        Files.move(file.toPath(), oldFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        createFile();
    }

    /**
     * Copies the default config from the jar to the plugin folder.
     * @throws IOException when an I/O error occurs reading or writing.
     */
    public void createFile() throws IOException {
        // The files copy below will throw an error if the directories are not pre existing.
        file = new File(sm.getDataFolder(), filePath);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            Files.createDirectories(parentFile.toPath());
        }
        // Open the file and copy it to the plugin folder.
        InputStream is = sm.getResource(file.getName());
        Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        load();
    }

    /**
     * Saves the config to file.
     * NOTE: Comments will be removed!
     * @throws IOException when I/O error occurs
     */
    public void save() throws IOException {
        fileCon.save(file);
    }

    public void updateFile() throws IOException {
        InputStream includedFile = sm.getResource(file.getName());
        if (includedFile == null) {
            throw new UnsupportedOperationException("Config file" + file.getName() + " could not be loaded from jar file.");
        }
        InputStreamReader reader = new InputStreamReader(includedFile, StandardCharsets.UTF_8);
        FileConfiguration includedConfig = YamlConfiguration.loadConfiguration(reader);
        boolean updated = false;
        for (String key : includedConfig.getKeys(true)) {
            if (fileCon.isSet(key)) {
                continue;
            }
            fileCon.set(key, includedConfig.get(key));
            if (Utilities.isVersionAtLeast(Utilities.MinecraftVersion.V1_18_2)) {
                fileCon.setComments(key, includedConfig.getComments(key));
            }
            updated = true;
        }
        if (!updated) {
            return;
        }
        fileCon.options().header(includedConfig.options().header());
        sm.getLogger().info("Config file " + file.getName() + " has been updated with new values.");
        if (!Utilities.isVersionAtLeast(Utilities.MinecraftVersion.V1_18_2)) {
            sm.getLogger().warning("This means that comments have been removed.");
            sm.getLogger().info("If you need comments, you access a version with them at " + Utilities.GITHUB_DEFAULT_CONFIG);
        }
        save();
    }

}
