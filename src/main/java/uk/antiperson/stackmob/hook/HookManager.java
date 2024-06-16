package uk.antiperson.stackmob.hook;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.hook.hooks.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.HashSet;

public class HookManager {

    private final HashSet<Hook> hooks;
    private final StackMob sm;
    private ProtocolLibHook protocolLibHook;
    public HookManager(StackMob sm) {
        this.sm = sm;
        hooks = new HashSet<>();
    }

    /**
     * Register hooks on server load rather than enable, since some plugins are special.
     * @throws NoSuchMethodException if hook class not have a constructor.
     * @throws IllegalAccessException if hook class can not be accessed
     * @throws InvocationTargetException if class throws an exception in the constructor.
     * @throws InstantiationException if the class is abstract
     */
    public void registerOnLoad() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (sm.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            createInstance(WorldGuardHook.class).onLoad();
        }
    }

    /**
     * Register plugin hooks.
     * @throws NoSuchMethodException if hook class not have a constructor.
     * @throws IllegalAccessException if hook class can not be accessed
     * @throws InvocationTargetException if class throws an exception in the constructor.
     * @throws InstantiationException if the class is abstract
     */
    public void registerHooks() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        registerHook(WorldGuardHook.class);
        registerHook(MythicMobsStackHook.class);
        registerHook(MythicMobsNoStackHook.class);
        registerHook(McmmoHook.class);
        registerHook(CitizensHook.class);
        registerHook(JobsHook.class);
        registerHook(ProtocolLibHook.class);
        registerHook(ClearlaggHook.class);
        registerHook(MyPetHook.class);
    }

    /**
     * Register a  single hook class.
     * @param hookClass the class to register
     * @throws NoSuchMethodException if hook class not have a constructor.
     * @throws IllegalAccessException if hook class can not be accessed
     * @throws InvocationTargetException if class throws an exception in the constructor.
     * @throws InstantiationException if the class is abstract
     */
    private void registerHook(Class<? extends Hook> hookClass) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        HookMetadata hookMetadata = hookClass.getAnnotation(HookMetadata.class);
        if (!sm.getServer().getPluginManager().isPluginEnabled(hookMetadata.name())) {
            return;
        }
        if (!sm.getMainConfig().getConfig().isHookEnabled(hookMetadata.config())) {
            return;
        }
        Hook hook = createInstance(hookClass);
        if (hook instanceof Listener) {
            sm.getServer().getPluginManager().registerEvents((Listener) hook, sm);
        }
        hook.onEnable();
        hooks.add(hook);
    }

    /**
     * Create a new instance of the given class.
     * @param hookClass the class to create an instance of.
     * @return the new instance of this class.
     * @throws NoSuchMethodException if hook class not have a constructor.
     * @throws IllegalAccessException if hook class can not be accessed
     * @throws InvocationTargetException if class throws an exception in the constructor.
     * @throws InstantiationException if the class is abstract
     */
    private Hook createInstance(Class<? extends Hook> hookClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        for (Constructor<?> constructor : hookClass.getDeclaredConstructors()) {
            for (Parameter parameter : constructor.getParameters()) {
                if (parameter.getType().isAssignableFrom(StackMob.class)) {
                    return hookClass.getDeclaredConstructor(StackMob.class).newInstance(sm);
                }
            }
        }
        return hookClass.getConstructor().newInstance();
    }

    /**
     * Checks if the two given entities should not stack together.
     * @param first the first entity to check.
     * @param nearby entity to compare with.
     * @return if these entities should not stack.
     */
    public boolean checkHooks(StackEntity first, StackEntity nearby) {
        for (Hook hook : hooks) {
            if (hook instanceof ProtectionHook) {
                ProtectionHook ph = (ProtectionHook) hook;
                if (!ph.canStack(first.getEntity()) || !ph.canStack(nearby.getEntity())) {
                    return true;
                }
            } else if (hook instanceof PreventStackHook) {
                PreventStackHook smh = (PreventStackHook) hook;
                if (smh.isCustomMob(first.getEntity()) || smh.isCustomMob(nearby.getEntity())) {
                    return true;
                }
            } else if (hook instanceof StackableMobHook) {
                StackableMobHook smh = (StackableMobHook) hook;
                if (smh.isCustomMob(first.getEntity()) && smh.isCustomMob(nearby.getEntity())) {
                    if (!smh.isMatching(first.getEntity(), nearby.getEntity())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean spawnCheck(LivingEntity stackEntity) {
        for (Hook hook : hooks) {
            if (hook instanceof PreventStackHook) {
                PreventStackHook smh = (PreventStackHook) hook;
                if (smh.isCustomMob(stackEntity)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onSpawn(StackEntity entity) {
        for (Hook hook : hooks) {
            if (hook instanceof SpawnHook) {
                SpawnHook spawnHook = (SpawnHook) hook;
                spawnHook.onSpawn(entity.getEntity());
            }
        }
    }

    public LivingEntity spawnClone(Location location, StackEntity entity) {
        StackableMobHook smh = getApplicableHook(entity);
        if (smh != null) {
            return smh.spawnClone(location, entity.getEntity());
        }
        return null;
    }

    /**
     * Gets the applicable hook for this entity.
     * @param entity the entity to get the applicable hook for.
     * @return the applicable hook.
     */
    public StackableMobHook getApplicableHook(StackEntity entity) {
        for (Hook hook : hooks) {
            if (hook instanceof StackableMobHook) {
                StackableMobHook smh = (StackableMobHook) hook;
                if (smh.isCustomMob(entity.getEntity())) {
                    return smh;
                }
            }
        }
        return null;
    }

    public ProtocolLibHook getProtocolLibHook() {
        if (protocolLibHook != null) {
            return protocolLibHook;
        }
        for (Hook hook : hooks) {
            if (hook instanceof ProtocolLibHook) {
                protocolLibHook = (ProtocolLibHook) hook;
            }
        }
        return protocolLibHook;
    }

}
