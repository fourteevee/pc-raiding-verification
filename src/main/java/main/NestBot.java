package main;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import commands.CommandHub;
import commands.adminCommands.*;
import commands.leaderCommands.CommandEndAllRaids;
import commands.leaderCommands.CommandFullSkip;
import commands.leaderCommands.defaultRaid.*;
import commands.leaderCommands.CommandHeadcount;
import commands.leaderCommands.CommandLocation;
import commands.leaderCommands.eventRaid.CommandEventHeadcount;
import commands.leaderCommands.eventRaid.CommandEventStartRaid;
import commands.leaderCommands.exrlRaid.CommandExHeadcount;
import commands.leaderCommands.exrlRaid.CommandExStartRaid;
import commands.leaderCommands.wrRaid.CommandWrHeadcount;
import commands.leaderCommands.wrRaid.CommandWrStartRaid;
import commands.miscCommands.CommandCommands;
import commands.miscCommands.CommandExVerify;
import commands.miscCommands.CommandRuns;
import commands.miscCommands.CommandVerify;
import listeners.CommandListener;
import listeners.MessageListener;
import listeners.ReactionListener;
import listeners.ReadyListener;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import punishment.SuspensionHub;
import raids.FeedbackHub;
import raids.RaidHub;

import javax.security.auth.login.LoginException;

/**
 * The main discord bot class for RotMG discord server "Pest Control"
 * Created by MistaCat 10/7/2018
 */
@Getter
public class NestBot {

    public static CommandHub commands = new CommandHub();
    public static FeedbackHub feedback = new FeedbackHub();
    public static SuspensionHub suspensions = new SuspensionHub();
    public static RaidHub raidHub = new RaidHub();
    public static EventWaiter eventWaiter;
    public static JDA jda;

    public static void main(String[] args) {

        new Config();

        JDABuilder builder = new JDABuilder().setToken((String) Config.get("TOKEN"));

        //Njg1NjIyMzM0MDE0MzU3NTE5.Xrzz5A.c-d9iqdBnI_1yF8RrcqXL3lKzN0
        builder.setAutoReconnect(true);

        builder.setStatus(OnlineStatus.ONLINE);

        eventWaiter = new EventWaiter();

        registerCommands();
        registerListener(builder, eventWaiter);

        try {
            jda = builder.build();
        } catch ( LoginException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets the pest control discord server.
     * @return
     */
    public static Guild getGuild() {
        return jda.getGuildById((String) Config.get("GUILD"));
    }

    public static void registerListener(JDABuilder builder, EventWaiter eventWaiter){

        builder.addEventListeners(new CommandListener());
        builder.addEventListeners(new MessageListener());
        builder.addEventListeners(new ReadyListener());
        builder.addEventListeners(new ReactionListener(eventWaiter));
        builder.addEventListeners(feedback);
        builder.addEventListeners(raidHub);
        builder.addEventListeners(eventWaiter);

    }

    private static void registerCommands() {
        commands.add(new CommandBeginRaid());
        commands.add(new CommandStartRaid());
        commands.add(new CommandEndRaid());
        commands.add(new CommandAbortRaid());
        commands.add(new CommandExStartRaid());
        commands.add(new CommandEventStartRaid());
        commands.add(new CommandWrStartRaid());
        commands.add(new CommandFullSkip());
        commands.add(new CommandHeadcount());
        commands.add(new CommandExHeadcount());
        commands.add(new CommandEventHeadcount());
        commands.add(new CommandWrHeadcount());
        commands.add(new CommandLocation());
        commands.add(new CommandVerify());
        commands.add(new CommandExVerify());
        commands.add(new CommandSuspend());
        commands.add(new CommandUnsuspend());
        commands.add(new CommandBlacklist());
        commands.add(new CommandUnblacklist());
        commands.add(new CommandCommands());
        commands.add(new CommandEndAllRaids());
        commands.add(new CommandHeadcount());
        commands.add(new CommandEndHeadcount());
        commands.add(new CommandActivity());
        commands.add(new CommandPoll());
        commands.add(new CommandReboot());
        commands.add(new CommandTerminate());
        commands.add(new CommandRuns());
        commands.add(new CommandVerifyMessage());
        commands.add(new CommandExVerifyMessage());
        commands.add(new CommandQuota());
        commands.add(new CommandAddRun());
    }
}
