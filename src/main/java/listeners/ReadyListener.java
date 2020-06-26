package listeners;

import main.Config;
import main.NestBot;
import main.Rank;
import main.StatsJson;
import net.dv8tion.jda.api.entities.*;
import org.apache.commons.lang3.StringUtils;
import stats.Verification;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import punishment.BlacklistManager;
import utils.Utils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Calendar.DAY_OF_WEEK;

public class ReadyListener extends ListenerAdapter {


    @Override
    public void onReady(ReadyEvent e){

        String activity = ((String) Config.get("ACTIVITY")).split(" ")[0];
        String activityMessage = StringUtils.split((String) Config.get("ACTIVITY"), " ", 2)[1];

        switch (activity.toLowerCase()){
            case "listening":
                NestBot.jda.getPresence().setPresence(Activity.listening(activityMessage), false);
                break;
            case "streaming":
                String streamUrl = ((String) Config.get("ACTIVITY")).split(" ")[1];
                activityMessage = StringUtils.split((String) Config.get("ACTIVITY"), " ", 3)[2];
                NestBot.jda.getPresence().setPresence(Activity.streaming(activityMessage, streamUrl), false);
                break;
            case "watching":
                NestBot.jda.getPresence().setPresence(Activity.watching(activityMessage), false);
                break;
            case "playing":
                NestBot.jda.getPresence().setPresence(Activity.playing(activityMessage), false);
                break;
        }

        String serverlist = "\nThis Bot is running on following servers: \n";

        for (Guild g : e.getJDA().getGuilds()) {
            serverlist += "    - " + g.getName() + " {ServerID: " + g.getId() + ", Members: " + g.getMembers().size() + "} \n";
        }

        System.out.println(serverlist);

        scheduleQuota();
        BlacklistManager.setupBlacklist();
    }

    //Schedules Task for every Sunday to check how many Runs each RL has done and then reset.
    public static void scheduleQuota(){
        Calendar calendar = Calendar.getInstance();
        Map<Integer, Integer> dayToDelay = new HashMap<>();
        dayToDelay.put(Calendar.MONDAY, 6);
        dayToDelay.put(Calendar.TUESDAY, 5);
        dayToDelay.put(Calendar.WEDNESDAY, 4);
        dayToDelay.put(Calendar.THURSDAY, 3);
        dayToDelay.put(Calendar.FRIDAY, 2);
        dayToDelay.put(Calendar.SATURDAY, 1);
        dayToDelay.put(Calendar.SUNDAY, 0);
        int dayOfWeek = calendar.get(DAY_OF_WEEK);
        int delayInDays = dayToDelay.get(dayOfWeek);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                TextChannel quotaChannel = NestBot.getGuild().getTextChannelById((String) Config.get("RUNQUOTA_CHANNEL"));

                for (Member member : NestBot.getGuild().getMembersWithRoles(Rank.EX_RL.getRole())){
                    if (!Rank.getHighestRank(member).isAtLeast(Rank.SECURITY)){
                        long exrl_quota = StatsJson.getQuota(member.getId(), "EXRL_QUOTA");
                        if (exrl_quota < Long.parseLong((String) Config.get("EXRL_QUOTA"))){
                            quotaChannel.sendMessage(Rank.HEAD_RL.getRole().getAsMention() + " [EXRL] " + member.getAsMention() + "** hasn't completed " + Config.get("EXRL_QUOTA") + " runs!** Runs completed: " + exrl_quota).queue();
                        }
                    }
                }

                for (Member member : NestBot.getGuild().getMembersWithRoles(Rank.RL.getRole())){
                    if (!Rank.getHighestRank(member).isAtLeast(Rank.SECURITY) && !member.getRoles().contains(Rank.EX_RL.getRole())){
                        long rl_quota = StatsJson.getQuota(member.getId(), "RL_QUOTA");
                        if (rl_quota < Long.parseLong((String) Config.get("RL_QUOTA"))){
                            quotaChannel.sendMessage(Rank.HEAD_RL.getRole().getAsMention() + " [RL] " + member.getAsMention() + "** hasn't completed " + Config.get("RL_QUOTA") + " runs!** Runs completed: " + rl_quota).queue();
                        }
                    }
                }

                for (Member member : NestBot.getGuild().getMembersWithRoles(Rank.ALMOST_RL.getRole())){
                    if (!Rank.getHighestRank(member).isAtLeast(Rank.SECURITY) && !member.getRoles().contains(Rank.EX_RL.getRole()) && !member.getRoles().contains(Rank.RL.getRole())){
                        long arl_quota = StatsJson.getQuota(member.getId(), "ARL_QUOTA");
                        if (arl_quota < Long.parseLong((String) Config.get("ARL_QUOTA"))){
                            quotaChannel.sendMessage(Rank.HEAD_RL.getRole().getAsMention() + " [ARL] " + member.getAsMention() + "** hasn't completed " + Config.get("ARL_QUOTA") + " runs!** Runs completed: " + arl_quota).queue();
                        }
                    }
                }

                StatsJson.resetWeekly();

            }
        }, TimeUnit.DAYS.toMillis(delayInDays), TimeUnit.DAYS.toMillis(7));
    }

}
