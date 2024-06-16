package uk.antiperson.stackmob.hook.hooks;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.api.JobsPaymentEvent;
import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import com.gamingmesh.jobs.container.CurrencyType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.hook.Hook;
import uk.antiperson.stackmob.hook.HookMetadata;
import uk.antiperson.stackmob.hook.SpawnHook;
import uk.antiperson.stackmob.utils.Utilities;

@HookMetadata(name = "Jobs", config = "jobs.enabled")
public class JobsHook extends Hook implements SpawnHook, Listener {

    private StackEntity stackEntity;
    public JobsHook(StackMob sm) {
        super(sm);
    }

    @Override
    public void onSpawn(LivingEntity spawned) {
        spawned.setMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata(), new FixedMetadataValue(getPlugin(), true));
    }

    @EventHandler
    public void onJobsPrePayment(JobsPrePaymentEvent event) {
        if (event.getLivingEntity() == null || Utilities.IS_FOLIA) { // TODO: Make this work with Folia.
            return;
        }
        stackEntity = sm.getEntityManager().getStackEntity(event.getLivingEntity());
    }

    @EventHandler
    public void onJobsPayment(JobsPaymentEvent event) {
        if (stackEntity == null || sm.getMainConfig().getConfig().getJobHookMode() == JobHookMode.IGNORE) {
            return;
        }
        for (CurrencyType currencyType : CurrencyType.values()) {
            if (sm.getMainConfig().getConfig().getJobHookMode() == JobHookMode.PREVENT) {
                event.set(currencyType, 0);
                continue;
            }
            event.set(currencyType, event.get(currencyType) * stackEntity.getSize());
        }
        stackEntity = null;
    }

    public enum JobHookMode {
        PREVENT,
        IGNORE,
        MULTIPLY
    }
}
