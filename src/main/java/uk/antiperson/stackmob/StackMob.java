package uk.antiperson.stackmob;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.Listener;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import uk.antiperson.stackmob.commands.Commands;
import uk.antiperson.stackmob.config.EntityTranslation;
import uk.antiperson.stackmob.config.MainConfig;
import uk.antiperson.stackmob.entity.EntityManager;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.entity.traits.TraitManager;
import uk.antiperson.stackmob.hook.HookManager;
import uk.antiperson.stackmob.listeners.*;
import uk.antiperson.stackmob.tasks.MergeTask;
import uk.antiperson.stackmob.tasks.TagTask;
import uk.antiperson.stackmob.utils.ItemTools;
import uk.antiperson.stackmob.utils.Updater;
import uk.antiperson.stackmob.utils.Utilities;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class StackMob extends JavaPlugin {

    private NamespacedKey stackKey = new NamespacedKey(this, "stack-size");
    private NamespacedKey waitKey = new NamespacedKey(this, "wait-key");
    private NamespacedKey toolKey = new NamespacedKey(this, "stack-tool");

    private MainConfig config;
    private EntityTranslation entityTranslation;
    private TraitManager traitManager;
    private HookManager hookManager;
    private EntityManager entityManager;
    private Updater updater;
    private ItemTools itemTools;

    @Override
    public void onLoad() {
        hookManager = new HookManager(this);
        try {
            hookManager.registerOnLoad();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            getLogger().log(Level.SEVERE, "There was a problem registering hooks. Features won't work.");
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        getLogger().info("StackMob v" + getDescription().getVersion() + " by antiPerson and contributors");
        traitManager = new TraitManager(this);
        entityManager = new EntityManager(this);
        config = new MainConfig(this);
        entityTranslation = new EntityTranslation(this);
        getLogger().info("Loading config files...");
        loadConfig();
        getLogger().info("Registering hooks and trait checks...");
        try{
            getTraitManager().registerTraits();
            getHookManager().registerHooks();
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            getLogger().log(Level.SEVERE, "There was a problem registering traits and hooks. Features won't work.");
            e.printStackTrace();
        }
        getLogger().info("Registering events, commands and tasks...");
        try {
            registerEvents();
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        register();
        updater = new Updater(this, 29999);
        getUpdater().checkUpdate().whenComplete(((updateResult, throwable) -> {
            switch (updateResult.getResult()) {
                case NONE:
                    getLogger().info("No update is currently available.");
                    break;
                case ERROR:
                    getLogger().info("There was an error while getting the latest update.");
                    break;
                case AVAILABLE:
                    getLogger().info("A new version is currently available. (" + updateResult.getNewVersion() + ")");
                    break;
            }
        }));
        Metrics metrics = new Metrics(this);
        metrics.addCustomChart(new Metrics.SimplePie("stackmobbridge", () -> String.valueOf(Bukkit.getPluginManager().isPluginEnabled("StackMobBridge"))));
        if (metrics.isEnabled()) {
            getLogger().info("bStats anonymous data collection has been enabled!");
        }
        if(getMainConfig().isAutoCheckEnabled())
            this.checkMob();
        itemTools = new ItemTools(this);
    }

    private void checkMob()
    {
        StackMob plugin = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                AtomicInteger n = new AtomicInteger();
                for(World world:Bukkit.getWorlds())
                {
                    plugin.getLogger().info("Checking stack for mob in world " + world.getName());
                    for (Entity entity : world.getEntities()) {
                        if (!(entity instanceof Mob)) {
                            continue;
                        }
                        LivingEntity livingEntity = (LivingEntity) entity;
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            if (plugin.getMainConfig().isEntityBlacklisted(livingEntity)) {
                                return;
                            }
                            if (plugin.getEntityManager().isStackedEntity(livingEntity)) {
                                return;
                            }
                            StackEntity original = plugin.getEntityManager().getStackEntity(livingEntity);
                            Integer[] searchRadius = plugin.getMainConfig().getStackRadius(entity.getType());
                            for (Entity nearbyEntity : entity.getNearbyEntities(searchRadius[0], searchRadius[1], searchRadius[2])) {
                                if (!(nearbyEntity instanceof Mob)) {
                                    continue;
                                }
                                StackEntity nearby = plugin.getEntityManager().getStackEntity((LivingEntity) nearbyEntity);
                                if (plugin.getMainConfig().getStackThresholdEnabled(nearbyEntity.getType()) && nearby.getSize() == 1) {
                                    continue;
                                }
                                if (!original.checkNearby(nearby)) {
                                    continue;
                                }
                                if (nearby.merge(original)) {
                                    return;
                                }
                            }
                            original.setSize(1);
                            plugin.getHookManager().onSpawn(original);
                            n.getAndIncrement();
                        });
                    }
                }
                plugin.getLogger().info(n.toString() + " entities found for stacking");
            }
        }.runTaskTimer(this, 0, getMainConfig().getAutoCheckInterval());
    }

    private void loadConfig() {
        try {
            getMainConfig().load();
            if (getMainConfig().isSet("check-area.x")) {
                getLogger().info("Old config detected. Renaming to config.old and making a new one.");
                getMainConfig().makeOld();
                downloadBridge();
            }
            getEntityTranslation().load();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "There was a problem loading the configuration file. Features won't work.");
            e.printStackTrace();
        }
    }

    private void register() {
        int stackInterval = getMainConfig().getStackInterval();
        new MergeTask(this).runTaskTimer(this, 5, stackInterval);
        int tagInterval = getMainConfig().getTagNearbyInterval();
        new TagTask(this).runTaskTimer(this, 5, tagInterval);
        PluginCommand command = getCommand("stackmob");
        Commands commands = new Commands(this);
        command.setExecutor(commands);
        command.setTabCompleter(commands);
        commands.registerSubCommands();
    }

    private void registerEvents() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
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
    }

    private void registerEvent(Class<? extends Listener> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ListenerMetadata listenerMetadata = clazz.getAnnotation(ListenerMetadata.class);
        if (listenerMetadata != null) {
            if (!getMainConfig().isListenerEnabled(listenerMetadata.config())) {
                return;
            }
            if (getMainConfig().isSet(listenerMetadata.config())) {
                if (getMainConfig().getBoolean(listenerMetadata.config())) {
                    return;
                }
            }
        }
        Listener listener = clazz.getDeclaredConstructor(StackMob.class).newInstance(this);
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private void downloadBridge() {
        getLogger().info("Installing StackMobBridge (utility to convert legacy mob stacks)...");
        File file = new File(getDataFolder().getParent(), "StackMobBridge.jar");
        String bridgeUrl = "http://aqua.api.spiget.org/v2/resources/45495/download";
        Utilities.downloadFile(file, bridgeUrl).whenComplete(((downloadResult, throwable) -> {
            if (downloadResult == Utilities.DownloadResult.ERROR) {
                getLogger().log(Level.SEVERE,"There was an issue while downloading StackMobBridge.");
                getLogger().log(Level.SEVERE, "This means that mob stacks will not be converted to the newer format.");
                return;
            }
            if (getServer().getPluginManager().getPlugin("StackMobBridge") != null) {
                return;
            }
            try {
                Plugin plugin = getPluginLoader().loadPlugin(file);
                getPluginLoader().enablePlugin(plugin);
            } catch (InvalidPluginException e) {
                e.printStackTrace();
            }
        }));
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

    public Updater getUpdater() {
        return updater;
    }

    public NamespacedKey getStackKey() {
        return stackKey;
    }

    public NamespacedKey getWaitKey() {
        return waitKey;
    }

    public NamespacedKey getToolKey() {
        return toolKey;
    }

    public ItemTools getItemTools() {
        return itemTools;
    }
}
