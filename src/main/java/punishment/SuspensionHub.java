package punishment;

import net.dv8tion.jda.api.entities.User;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A class that manages all the suspensions for pest control.
 * Created by MistaCat 10/9/2018
 */
public class SuspensionHub {
    public static List<Suspension> activeSuspensions = new CopyOnWriteArrayList<>();

    private ScheduledExecutorService TIMER = new ScheduledThreadPoolExecutor(1);

    public SuspensionHub() {
        TIMER.scheduleAtFixedRate(() -> {
            activeSuspensions.forEach(suspension -> {
                if (suspension.getSuspensionTime() < System.currentTimeMillis() && suspension.getSuspensionTime() != -1)
                    suspension.finishSuspension();
            });
        }, 0L, 1L,  TimeUnit.SECONDS);
    }

    /**
     * Gets a user's suspension if they have one. Returns null if not.
     * @param user
     * @return
     */
    public static Suspension getUserSuspension(User user) {
        for (Suspension sus : activeSuspensions)
            if (sus.getRecipient() == user)
                return sus;

        return null;
    }
}
