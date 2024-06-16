package uk.antiperson.stackmob;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import uk.antiperson.stackmob.commands.Commands;
import uk.antiperson.stackmob.config.EntityTranslation;
import uk.antiperson.stackmob.config.MainConfig;
import uk.antiperson.stackmob.entity.EntityManager;
import uk.antiperson.stackmob.entity.traits.TraitManager;
import uk.antiperson.stackmob.hook.HookManager;
import uk.antiperson.stackmob.listeners.*;
import uk.antiperson.stackmob.packets.PlayerManager;
import uk.antiperson.stackmob.scheduler.BukkitScheduler;
import uk.antiperson.stackmob.scheduler.FoliaScheduler;
import uk.antiperson.stackmob.scheduler.Scheduler;
import uk.antiperson.stackmob.tasks.MergeTask;
import uk.antiperson.stackmob.tasks.TagCheckTask;
import uk.antiperson.stackmob.tasks.TagMoveTask;
import uk.antiperson.stackmob.utils.ItemTools;
import uk.antiperson.stackmob.utils.Updater;
import uk.antiperson.stackmob.utils.Utilities;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class StackMob extends JavaPlugin {

    private final NamespacedKey stackKey = new NamespacedKey(this, "stack-size");
    private final NamespacedKey toolKey = new NamespacedKey(this, "stack-tool");

    private MainConfig config;
    private EntityTranslation entityTranslation;
    private TraitManager traitManager;
    private HookManager hookManager;
    private EntityManager entityManager;
    private Updater updater;
    private ItemTools itemTools;
    private PlayerManager playerManager;
    private BukkitAudiences adventure;
    private Scheduler scheduler;

    private boolean stepDamageError;

    @Override
    public void onLoad() {
        hookManager = new HookManager(this);
        try {
            hookManager.registerOnLoad();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            getLogger().log(Level.SEVERE, "There was a problem registering hooks. Features won't work.");
            e.printStackTrace();
        }
        scheduler = Utilities.IS_FOLIA ? new FoliaScheduler() : new BukkitScheduler();
    }

    @Override
    public void onEnable() {
        adventure = BukkitAudiences.create(this);
        traitManager = new TraitManager(this);
        entityManager = new EntityManager(this);
        config = new MainConfig(this);
        entityTranslation = new EntityTranslation(this);
        updater = new Updater(this, 29999);
        itemTools = new ItemTools(this);
        playerManager = new PlayerManager(this);
        getLogger().info("StackMob v" + getDescription().getVersion() + " by antiPerson and contributors.");
        getLogger().info("GitHub: " + Utilities.GITHUB + " Discord: " + Utilities.DISCORD);
        getLogger().info("Loading config files...");
        try {
            getMainConfig().init();
            getEntityTranslation().reloadConfig();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "There was a problem loading the configuration file.");
            e.printStackTrace();
        }
        getLogger().info("Registering hooks and trait checks...");
        try {
            getHookManager().registerHooks();
            getTraitManager().registerTraits();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        getLogger().info("Registering events, commands and tasks...");
        try {
            registerEvents();
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        PluginCommand command = getCommand("stackmob");
        Commands commands = new Commands(this);
        command.setExecutor(commands);
        command.setTabCompleter(commands);
        commands.registerSubCommands();
        int stackInterval = getMainConfig().getConfig().getStackInterval();
        getScheduler().runGlobalTaskTimer(this, new MergeTask(this), 20, stackInterval);
        int tagInterval = getMainConfig().getConfig().getTagNearbyInterval();
        getScheduler().runGlobalTaskTimer(this, new TagCheckTask(this), 30, tagInterval);
        if (getMainConfig().getConfig().isUseArmorStand()) {
            getScheduler().runGlobalTaskTimer(this, new TagMoveTask(this), 10, 1);
        }
        getLogger().info("Detected server version " + Utilities.getMinecraftVersion());
        if (getHookManager().getProtocolLibHook() == null) {
            getLogger().warning("ProtocolLib could not be found (or has been disabled). The display name visibility setting 'NEARBY' will not work unless this is fixed.");
        }
        getEntityManager().registerAllEntities();
        getUpdater().checkUpdate().whenComplete(((updateResult, throwable) -> {
            switch (updateResult.getResult()) {
                case NONE: getLogger().info("No update is currently available."); break;
                case ERROR: getLogger().info("There was an error while getting the latest update."); break;
                case AVAILABLE: getLogger().info("A new version is currently available. (" + updateResult.getNewVersion() + ")"); break;
            }
        }));
        if (!Utilities.isPaper()) {
            getLogger().warning("It has been detected that you are not using Paper (https://papermc.io).");
            getLogger().warning("StackMob makes use of Paper's API, which means you're missing out on features.");
        }
        new Metrics(this, 522);
    }

    @Override
    public void onDisable() {
        getEntityManager().unregisterAllEntities();
        Bukkit.getOnlinePlayers().forEach(player -> getPlayerManager().stopWatching(player));
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
    }

    private void registerEvents() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        registerEvent(PlayerArmorStandListener.class);
        registerEvent(BucketListener.class);
        registerEvent(DeathListener.class);
        registerEvent(TransformListener.class);
        registerEvent(BreedInteractListener.class);
        registerEvent(TagInteractListener.class);
        registerEvent(DyeListener.class);
        registerEvent(ShearListener.class);
        registerEvent(ExplosionListener.class);
        registerEvent(DropListener.class);
        registerEvent(TameListener.class);
        registerEvent(SlimeListener.class);
        registerEvent(SpawnListener.class);
        registerEvent(TargetListener.class);
        registerEvent(PlayerListener.class);
        registerEvent(BeeListener.class);
        registerEvent(LeashListener.class);
        registerEvent(EquipListener.class);
        if (Utilities.isVersionAtLeast(Utilities.MinecraftVersion.V1_20_4)) {
            registerEvent(KnockbackListener.class);
        }
        if (Utilities.isPaper()) {
            registerEvent(RemoveListener.class);
            return;
        }
        registerEvent(ChunkListener.class);
    }

    private void registerEvent(Class<? extends Listener> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ListenerMetadata listenerMetadata = clazz.getAnnotation(ListenerMetadata.class);
        if (listenerMetadata != null) {
            if (!getMainConfig().getConfigFile().isSet(listenerMetadata.config())) {
                return;
            }
            if (!getMainConfig().getConfigFile().getBoolean(listenerMetadata.config())) {
                return;
            }
        }
        Listener listener = clazz.getDeclaredConstructor(StackMob.class).newInstance(this);
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public EntityTranslation getEntityTranslation() {
        return entityTranslation;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public MainConfig getMainConfig() {
        return config;
    }

    public TraitManager getTraitManager() {
        return traitManager;
    }

    public HookManager getHookManager() {
        return hookManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public Updater getUpdater() {
        return updater;
    }

    public NamespacedKey getStackKey() {
        return stackKey;
    }

    public NamespacedKey getToolKey() {
        return toolKey;
    }

    public ItemTools getItemTools() {
        return itemTools;
    }

    public BukkitAudiences getAdventure() {
        if (adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    public boolean isStepDamageError() {
        return stepDamageError;
    }

    public void setStepDamageError(boolean stepDamageError) {
        this.stepDamageError = stepDamageError;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}
