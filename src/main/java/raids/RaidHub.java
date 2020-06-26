package raids;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import lombok.AllArgsConstructor;
import lombok.Getter;
import main.Constants;
import main.NestBot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.Utils;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * Manages all the current nest runs that are currently active.
 * Created by MistaCat 10/7/2018
 */
@Getter
public class RaidHub extends ListenerAdapter {


    public enum RaidType {
        DEFAULT_RAID,
        EXRL_RAID,
        EVENT_RAID,
        WR_RAID;

        public Long getRaidChannelId(){
            switch (this){
                case DEFAULT_RAID:
                    return Constants.RAID_CHANNEL;
                case EXRL_RAID:
                    return Constants.EXRAID_CHANNEL;
                case EVENT_RAID:
                    return Constants.EVENTRAID_CHANNEL;
                case WR_RAID:
                    return Constants.WRRAID_CHANNEL;
                default:
                    return 0L;
            }
        }
    }

    public static List<Raid> activeRaids = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService TIMER = new ScheduledThreadPoolExecutor(2);

    @Override
    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
        if (!isRaidChannel(event.getChannelJoined()))
            return;

        Utils.updateChannelPerms(event.getChannelJoined(), event.getMember(), EnumSet.of(Permission.VOICE_CONNECT), EnumSet.noneOf(Permission.class));
    }

    @Override
    public void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent event) {
        if (isRaidChannel(event.getChannelJoined())){
            Utils.updateChannelPerms(event.getChannelJoined(), event.getMember(), EnumSet.of(Permission.VOICE_CONNECT), EnumSet.noneOf(Permission.class));
        }

        if (isRaidChannel(event.getChannelLeft())){
            Utils.updateChannelPerms(event.getChannelJoined(), event.getMember(), EnumSet.of(Permission.VOICE_CONNECT), EnumSet.noneOf(Permission.class));
            if (getRaid(event.getChannelLeft()).isRaidActive())
                return;
            Utils.updateChannelPerms(event.getChannelLeft(), event.getMember(), EnumSet.noneOf(Permission.class), EnumSet.noneOf(Permission.class));
        }
    }

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        if (!isRaidChannel(event.getChannelLeft()))
            return;

        if (getRaid(event.getChannelLeft()).isRaidActive())
            return;

        Utils.updateChannelPerms(event.getChannelLeft(), event.getMember(), EnumSet.noneOf(Permission.class), EnumSet.noneOf(Permission.class));
    }

    /**
     * Starts a raid. set size to 0 for infinite, and set timer to -1 for no timer. Set null for no location
     * @param leader
     * @param size
     * @param timer
     * @param location
     */
    public static void startRaid(@Nonnull Member leader, int size, int timer, String location, @Nonnull RaidType raidType) {
        activeRaids.add(new Raid(leader, size, timer, location, raidType));
    }

    /**
     * Gets all active raids of type.
     * @param raidType
     * @return Raids List
     */
    public static List<Raid> getRaidsByType(RaidType raidType) {
        return activeRaids.stream().filter(raid -> raid.getRaidType().equals(raidType)).collect(Collectors.toList());
    }

   /**
     * Gets an active raid from a discord user. Will return null if they have not started a raid.
     * @param leader
     * @return
     */
    public static Raid getRaid(Member leader) {
        for (Raid raid : activeRaids) {
            if (raid.getLeader()==(leader))
                return raid;
        }

        return null;
    }

    /**
     * Gets an active raid from a message id (raidMsg). Will return null if no raid is found.
     * @param messageId
     * @return
     */
    public static Raid getRaid(String messageId) {
        for (Raid raid : activeRaids) {
            if (raid.getRaidMsg().getId().equals(messageId)) return raid;
        }

        return null;
    }

    /**
     * Checks if a discord user is currently leading a raid.
     * @param leader
     * @return
     */
    public static boolean isLeading(Member leader) {
        return getRaid(leader) != null;
    }

    /**
     * Gets an active raid from it's voice channel. Will return null if the channel is not a raid channel.
     * @param channel
     * @return
     */
    public static Raid getRaid(VoiceChannel channel) {
        for (Raid raid : activeRaids) {
            if (raid.getRaidRoom() == channel)
                return raid;
        }

        return null;
    }

    public static boolean isRaidChannel(VoiceChannel channel) { return getRaid(channel) != null; }
}
