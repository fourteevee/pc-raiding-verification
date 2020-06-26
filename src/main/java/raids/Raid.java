package raids;

import lombok.Getter;
import main.*;
import main.Emote;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import utils.Utils;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

import static raids.RaidHub.RaidType;

/**
 * The base class for a nest run or "raid". Everything will be handled here.
 * Created by MistaCat 10/7/2018
 */
@Getter
public class Raid {

    //List to keep order of users that react, because discord returns random list
    private ArrayList<Member> nestKeyReactions = new ArrayList<>();
    private ArrayList<Member> eventKeyReactions = new ArrayList<>();
    private ArrayList<Member> knightReactions = new ArrayList<>();
    private ArrayList<Member> mysticReactions = new ArrayList<>();
    private ArrayList<Member> puriReactions = new ArrayList<>();
    private ArrayList<Member> nitroReactions = new ArrayList<>();

    private VoiceChannel raidRoom;
    private Message raidMsg;
    private Member leader;
    private Long startTime;
    private String location;
    private RaidType raidType;

    private boolean raidActive = false;
    private boolean finished = false;
    private boolean failed;
    private boolean isFullSkipped;
    private int countdownTime;
    private int countdown;
    private ScheduledFuture<?> countdownTimer;
    private final Long COUNTDOWN_INTERVAL = 5L;
    private final ScheduledExecutorService TIMER = new ScheduledThreadPoolExecutor(2);


    public Raid(Member leader, int size, int time, String location, RaidType raidType) {

        VoiceChannel raidRoom = NestBot.getGuild().getTextChannelById(raidType.getRaidChannelId()).getParent().createVoiceChannel("New raid").complete();
        this.raidRoom = raidRoom;
        setVoiceCap(size);
        this.leader = leader;
        this.countdownTime = time;
        this.raidMsg = Utils.sendMessage(NestBot.getGuild().getTextChannelById(raidType.getRaidChannelId()), "A new raid has begun");
        this.location = (location == null) ? "No location has been set" : location;
        this.raidType = raidType;

        initializeRaid();

    }

    public void initializeRaid() {
        this.failed = false;
        this.raidActive = false;
        raidMsg.delete().queue();

        //reset the lists of users that reacted to get location so that again 3 people can react and get location
        nestKeyReactions = new ArrayList<>();
        eventKeyReactions = new ArrayList<>();
        knightReactions = new ArrayList<>();
        mysticReactions = new ArrayList<>();
        puriReactions = new ArrayList<>();
        nitroReactions = new ArrayList<>();


        countdown = countdownTime;
        raidRoom.getManager().setName(raidType.equals(RaidType.EVENT_RAID) ? "Event starting soon!" : "Nest starting soon!").queue();
        this.raidMsg = Utils.sendMessage(NestBot.getGuild().getTextChannelById(raidType.getRaidChannelId()), "A new raid has begun, @here");

        raidMsg = raidMsg.editMessage(raidMsg.getContentRaw()).embed(createRaidMsg().build()).complete();

        Role eligibleRole = NestBot.getGuild().getRoleById(Constants.VERIFIED);

        if (this.raidType.equals(RaidType.EXRL_RAID)) {
            eligibleRole = NestBot.getGuild().getRoleById(Constants.EXTERMINATOR);
        }

        raidRoom.getPermissionOverrides().stream().forEach(permissionOverride -> permissionOverride.delete().complete());
        Utils.updateChannelPerms(raidRoom, NestBot.getGuild().getPublicRole(), EnumSet.noneOf(Permission.class), EnumSet.of(Permission.VOICE_CONNECT, Permission.VOICE_SPEAK));
        Utils.updateChannelPerms(raidRoom, eligibleRole, EnumSet.of(Permission.VOICE_CONNECT), EnumSet.noneOf(Permission.class));
        for (Rank rank : Rank.values())
            if (rank.isCanSpeak())
                Utils.updateChannelPerms(raidRoom, NestBot.getGuild().getRoleById(rank.getID()), EnumSet.allOf(Permission.class), EnumSet.noneOf(Permission.class));

        if (countdown > 0) {
            countdownTimer = TIMER.scheduleAtFixedRate(() -> {
                if (raidActive)
                    countdownTimer.cancel(true);

                if (!raidActive && countdown <= 0)
                    startRaid();
                else if (!raidActive)
                    updateRaidMsg();

                countdown -= COUNTDOWN_INTERVAL;
            }, 0L, COUNTDOWN_INTERVAL, TimeUnit.SECONDS);
        }

        String quota = "ARL_QUOTA";

        if (NestBot.getGuild().getMembersWithRoles(Rank.EX_RL.getRole()).contains(leader)){
            quota = "EXRL_QUOTA";
        } else if (NestBot.getGuild().getMembersWithRoles(Rank.RL.getRole()).contains(leader)){
            quota = "RL_QUOTA";
        }

        StatsJson.incrementQuota(leader.getId(), quota);
    }

    public void startRaid() {
        this.raidActive = true; //Activate the raid
        this.countdown = 0; //Ensure timer is at zero in case a manual start for a timed raid.
        this.startTime = System.currentTimeMillis();

        Role eligibleRole = NestBot.getGuild().getRoleById(Constants.VERIFIED);

        if (this.raidType.equals(RaidType.EXRL_RAID)) {
            eligibleRole = NestBot.getGuild().getRoleById(Constants.EXTERMINATOR);
        }

        Utils.updateChannelPerms(raidRoom, eligibleRole, EnumSet.noneOf(Permission.class), EnumSet.of(Permission.VOICE_CONNECT));

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(leader.getEffectiveName() + "'s raid has begun!");
        embedBuilder.setThumbnail(leader.getUser().getAvatarUrl());
        embedBuilder.setColor(new Color(255, 0, 0));
        embedBuilder.setDescription("Please wait until another leader begins a raid!");
        raidMsg = raidMsg.editMessage(embedBuilder.build()).complete();

        raidRoom.getManager().setName(raidType.equals(RaidType.EVENT_RAID) ? leader.getEffectiveName() + "'s Event Raid." : leader.getEffectiveName() + "'s Nest Raid.").submit();

        List<Member> reactedUsers = new ArrayList<>();
        for (MessageReaction messageReaction : raidMsg.getReactions()) {
            List<User> users = messageReaction.retrieveUsers().complete();
            for (Member voiceMember : raidRoom.getMembers()) {
                if (users.contains(voiceMember.getUser())){
                    reactedUsers.add(voiceMember);
                }
            }
        }
        ListUtils.subtract(raidRoom.getMembers(), reactedUsers).forEach(member -> {
            if (!Rank.getHighestRank(member).isAtLeast(Rank.TRIAL_RL)){
                raidRoom.getGuild().kickVoiceMember(member).queue();
            }
        });
    }

    public void abortRaid() {
        this.finished = true;
        this.failed = true;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(leader.getEffectiveName() + "'s raid was a failure!");
        embedBuilder.setThumbnail(leader.getUser().getAvatarUrl());
        embedBuilder.setColor(new Color(255, 0, 0));
        embedBuilder.setDescription("The raid has been aborted as nobody is left in the dungeon to complete it!");
        embedBuilder.setFooter("Awaiting raid leader's decision.");
        raidMsg.editMessage(embedBuilder.build()).submit();
    }

    public void completeRaid(boolean newRun, int playersLeft) {
        this.raidActive = false;
        raidRoom.getMembers().stream().forEach(member -> StatsJson.addCompletedRaid(member.getId()));
        FeedbackHub.activeFeedback.add(new LeaderFeedback(leader, raidRoom.getMembers()));
        logRaid(playersLeft);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(leader.getEffectiveName() + "'s raid was a success!");
        embedBuilder.setThumbnail(leader.getUser().getAvatarUrl());
        embedBuilder.setColor(new Color(0, 0, 255));
        embedBuilder.setDescription("If you were a part of the run please enter the feed back channel and give feedback and rating for the leader!" +
                (newRun ? "\n\nAnother run will be starting soon! Please stay in the channel if you wish to participate." : ""));
        raidMsg.editMessage(embedBuilder.build()).submit();

        if (newRun)
            TIMER.schedule(this::initializeRaid, 20L, TimeUnit.SECONDS);
        else
            TIMER.schedule(this::endRaid, 20L, TimeUnit.SECONDS);
    }

    private void logRaid(int players) {
        String playerNames = "";
        for (Member member : getRaidRoom().getMembers()){
            String removePrefix = "";
            switch (Rank.getHighestRank(member)){
                case OWNER:
                    removePrefix = "!!";
                    break;
                case ADMIN:
                    removePrefix = "!";
                    break;
                case HEAD_RL:
                    removePrefix = "\"";
                    break;
                case EX_RL:
                    removePrefix = "$";
                    break;
                case RL:
                    removePrefix = "'";
                    break;
                case ALMOST_RL:
                    removePrefix = "()";
                    break;
                case TRIAL_RL:
                    removePrefix = ")";
                    break;
                case OFFICER:
                    removePrefix = "#";
                    break;
                case SECURITY:
                    removePrefix = "*";
                    break;

            }
            playerNames += ", " + StringUtils.replace(member.getEffectiveName(), removePrefix, "", 1);
        }

        playerNames = playerNames.replaceFirst(", ", "");

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(leader.getEffectiveName() + " completed a raid!");
        embedBuilder.setDescription("Date Completed: " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) +
                "\nTime Taken: " + Utils.formatTimeFull(System.currentTimeMillis() - startTime) +
                "\nPlayers remaining in run (" + getRaidRoom().getMembers().size() + "): " + playerNames);
        String nestUsers = "";
        for (User user : raidMsg.retrieveReactionUsers(Emote.NEST.getEmote()).complete()){
            if (!user.isBot()) {
                nestUsers += user.getAsMention() + "\n";
            }
        }
        String nestKeyUsers = "";
        for (User user : raidMsg.retrieveReactionUsers(Emote.NEST_KEY.getEmote()).complete()){
            if (!user.isBot()) {
                nestKeyUsers += user.getAsMention() + "\n";
            }
        }
        String qotUsers = "";
        for (User user : raidMsg.retrieveReactionUsers(Emote.QOT.getEmote()).complete()){
            if (!user.isBot()) {
                qotUsers += user.getAsMention() + "\n";
            }
        }
        String priestUsers = "";
        for (User user : raidMsg.retrieveReactionUsers(Emote.PRIEST.getEmote()).complete()){
            if (!user.isBot()) {
                priestUsers += user.getAsMention() + "\n";
            }
        }
        String warriorUsers = "";
        for (User user : raidMsg.retrieveReactionUsers(Emote.WARRIOR.getEmote()).complete()){
            if (!user.isBot()) {
                warriorUsers += user.getAsMention() + "\n";
            }
        }
        String pallyUsers = "";
        for (User user : raidMsg.retrieveReactionUsers(Emote.PALLY.getEmote()).complete()){
            if (!user.isBot()) {
                pallyUsers += user.getAsMention() + "\n";
            }
        }
        String knightUsers = "";
        for (User user : raidMsg.retrieveReactionUsers(Emote.KNIGHT.getEmote()).complete()){
            if (!user.isBot()) {
                knightUsers += user.getAsMention() + "\n";
            }
        }
        String mysticUsers = "";
        for (User user : raidMsg.retrieveReactionUsers(Emote.MYSTIC.getEmote()).complete()){
            if (!user.isBot()) {
                mysticUsers += user.getAsMention() + "\n";
            }
        }
        String slowUsers = "";
        for (User user : raidMsg.retrieveReactionUsers(Emote.SLOW.getEmote()).complete()){
            if (!user.isBot()) {
                slowUsers += user.getAsMention() + "\n";
            }
        }
        String nitroUsers = "";
        for (User user : raidMsg.retrieveReactionUsers(Emote.NITRO.getEmote()).complete()){
            if (!user.isBot()) {
                nitroUsers += user.getAsMention() + "\n";
            }
        }
        String dungeonUsers = "";
        for (User user : raidMsg.retrieveReactionUsers(Emote.DUNGEON.getEmote()).complete()){
            if (!user.isBot()) {
                dungeonUsers += user.getAsMention() + "\n";
            }
        }
        String eventKeyUsers = "";
        for (User user : raidMsg.retrieveReactionUsers(Emote.EVENT_KEY.getEmote()).complete()){
            if (!user.isBot()) {
                eventKeyUsers += user.getAsMention() + "\n";
            }
        }

        embedBuilder.addField(Emote.NEST.display(), nestUsers.isEmpty() ? "No users" : nestUsers, true);
        embedBuilder.addField(Emote.NEST_KEY.display(), nestKeyUsers.isEmpty() ? "No users" : nestKeyUsers, true);
        embedBuilder.addField(Emote.QOT.display(), qotUsers.isEmpty() ? "No users" : qotUsers, true);
        embedBuilder.addField(Emote.PRIEST.display(), priestUsers.isEmpty() ? "No users" : priestUsers, true);
        embedBuilder.addField(Emote.WARRIOR.display(), warriorUsers.isEmpty() ? "No usders" : warriorUsers, true);
        embedBuilder.addField(Emote.PALLY.display(), pallyUsers.isEmpty() ? "No users" : pallyUsers, true);
        embedBuilder.addField(Emote.KNIGHT.display(), knightUsers.isEmpty() ? "No users" : knightUsers, true);
        embedBuilder.addField(Emote.MYSTIC.display(), mysticUsers.isEmpty() ? "No users" : mysticUsers, true);
        embedBuilder.addField(Emote.SLOW.display(), slowUsers.isEmpty() ? "No users" : slowUsers, true);
        embedBuilder.addField(Emote.NITRO.display(), nitroUsers.isEmpty() ? "No users" : nitroUsers, true);
        embedBuilder.addField(Emote.DUNGEON.display(), dungeonUsers.isEmpty() ? "No users" : dungeonUsers, true);
        embedBuilder.addField(Emote.EVENT_KEY.display(), eventKeyUsers.isEmpty() ? "No users" : eventKeyUsers, true);

        Utils.sendEmbed(NestBot.getGuild().getTextChannelById(Constants.RUN_LOGS), embedBuilder);
    }

    public void endRaid() {
        RaidHub.activeRaids.remove(this);
        this.finished = true;
        raidMsg.delete().submit();
        for (Member member : this.raidRoom.getMembers())
            Utils.moveUser(member, NestBot.getGuild().getVoiceChannelById(Constants.LOUNGE_VOICE));

        TIMER.schedule(() -> raidRoom.delete().submit(), 1L, TimeUnit.MINUTES);
        TIMER.shutdown();
    }

    private EmbedBuilder createRaidMsg() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(leader.getUser().getAvatarUrl());
        embedBuilder.setColor(new Color(0, 255, 0));

        if (raidType.equals(RaidType.EXRL_RAID)){
            embedBuilder.setTitle(leader.getEffectiveName() + " has started an Exterminator Fullskip Raid!");
            embedBuilder.setDescription("Make sure to join the Exterminator Runs voice channel if you are going to participate in the raid. \n" +
                    "Make sure to read Exterminator Rules before entering runs!\n" +
                    "Fake reacts will result in suspension.");
        } else if (raidType.equals(RaidType.EVENT_RAID)){
            embedBuilder.setTitle(leader.getEffectiveName() + " HAS STARTED AN EVENT RAID");
            embedBuilder.setDescription(
                    "Please react if you are interested in joining the event raid, or if you have keys for the event dungeon. The Raid Leader will let you know ahead of time what dungeon they are planning to run!\n" +
                    "\n" +
                    "Also, react with " + Emote.NITRO.getEmote().getAsMention() + " for early location!");
        } else if (raidType.equals(RaidType.WR_RAID)){
            embedBuilder.setDescription(
                    "Make sure to join the World record Runs voice channel if you are going to participate in the raid. \n" +
                    "Make sure to read world record Rules before entering runs!\n" +
                    "Participants in WR runs are selected by staff!\n" +
                    "Fake reacts will result in suspension.");
        } else {
            embedBuilder.setTitle(leader.getEffectiveName() + " has started a raid!");
            embedBuilder.setDescription("A new nest raid has started! \nJoin the newly opened channel if you wish to participate in the raid." +
                    "\nPlease react to anything that relates to you! Fake reacts may result in suspension!");
        }

        if (raidType.equals(RaidType.EVENT_RAID)){
            for (Emote emote : eventRaidEmotes){
                Utils.addReaction(raidMsg, emote.getEmote());
            }
            embedBuilder.addField("If you are attending the run, please react with", Emote.DUNGEON.display(), true);
            embedBuilder.addField("If you are bringing a KEY react with", Emote.EVENT_KEY.display(), true);
            embedBuilder.addField("If you are a nitro booster, and want early location, react with", Emote.NITRO.display(), true);
            embedBuilder.addField("If you are bringing a PRIEST react with", Emote.PRIEST.display(), true);
            embedBuilder.addField("If you are bringing a WARRIOR react with", Emote.WARRIOR.display(), true);
            embedBuilder.addField("If you are bringing a PALADIN react with", Emote.PALLY.display(), true);
            embedBuilder.addField("If you are bringing a KNIGHT react with", Emote.KNIGHT.display(), true);
            embedBuilder.addField("If you are bringing a PURI react with", Emote.PURI.display(), true);
        } else if (raidType.equals(RaidType.WR_RAID)){
            for (Emote emote : wrRaidEmotes){
                Utils.addReaction(raidMsg, emote.getEmote());
            }
            embedBuilder.addField("If you are attending the run, please react with", Emote.NEST.display(), true);
            embedBuilder.addField("If you are bringing a KEY react with", Emote.NEST_KEY.display(), true);
            embedBuilder.addField("If you are bringing a QOT react with", Emote.QOT.display(), true);
            embedBuilder.addField("If you are bringing a PRIEST react with", Emote.PRIEST.display(), true);
            embedBuilder.addField("If you are bringing a WARRIOR react with", Emote.WARRIOR.display(), true);
            embedBuilder.addField("If you are bringing a PALADIN react with", Emote.PALLY.display(), true);
            embedBuilder.addField("If you are bringing a KNIGHT react with", Emote.KNIGHT.display(), true);
            embedBuilder.addField("If you are bringing a MYSTIC react with", Emote.MYSTIC.display(), true);
            embedBuilder.addField("If you are bringing a SLOWING ABILITY react with", Emote.SLOW.display(), true);
        } else {
            for (Emote emote : defaulRaidEmotes){
                Utils.addReaction(raidMsg, emote.getEmote());
            }
            embedBuilder.addField("If you are attending the run, please react with", Emote.NEST.display(), true);
            embedBuilder.addField("If you are bringing a KEY react with", Emote.NEST_KEY.display(), true);
            embedBuilder.addField("If you are bringing a QOT react with", Emote.QOT.display(), true);
            embedBuilder.addField("If you are bringing a PRIEST react with", Emote.PRIEST.display(), true);
            embedBuilder.addField("If you are bringing a WARRIOR react with", Emote.WARRIOR.display(), true);
            embedBuilder.addField("If you are bringing a PALADIN react with", Emote.PALLY.display(), true);
            embedBuilder.addField("If you are bringing a KNIGHT react with", Emote.KNIGHT.display(), true);
            embedBuilder.addField("If you are bringing a MYSTIC react with", Emote.MYSTIC.display(), true);
            embedBuilder.addField("If you are bringing a SLOWING ABILITY react with", Emote.SLOW.display(), true);
            embedBuilder.addField("If you are a nitro booster, and want early location, react with", Emote.NITRO.display(), true);
        }

        if (countdown < 0)
            embedBuilder.setFooter("The raid will begin when the raid leader is ready");
        else
            embedBuilder.setFooter("The raid will begin in " + countdown + " seconds!");

        return embedBuilder;
    }

    private void updateRaidMsg() {
        EmbedBuilder embedBuilder = createRaidMsg();
        embedBuilder.setFooter("The raid will begin in " + countdown + " seconds!");
        raidMsg.editMessage(embedBuilder.build()).submit();
    }

    /**
     * @param location
     * @param notify If true it will notify all users that reacted with NEST_KEY that a new location has been set
     */
    public void setLocation(String location, boolean notify) {
        this.location = location;
        if (notify) {
            this.raidMsg.retrieveReactionUsers(Emote.NEST_KEY.getEmote()).queue(users -> {
                users.stream().filter(user -> !user.isBot()).forEach(user -> {
                    try {
                        user.openPrivateChannel().submit().get().sendMessage("A location has been set for " + this.leader.getEffectiveName() + "'s raid: **" + location + "**").submit();
                    } catch ( InterruptedException | ExecutionException e ) {
                        e.printStackTrace();
                    }
                });
            });
        }
    }

    /**
     * Starts the full skip poll in the Radeaid Channel. If minutes <= 0 it forces the full skip.
     * @param minutes
     */
    public void initiateFullSkip(int minutes) {

        if (minutes > 0) {
            Message fullSkipPoll =  Utils.sendEmbed(NestBot.getGuild().getTextChannelById(raidType.getRaidChannelId()), new EmbedBuilder().setTitle("Full Skip Poll")
                    .setDescription(leader.getEffectiveName() + " wants to do a fullskip, do you want to?"));

            fullSkipPoll.addReaction("👍").queue();
            fullSkipPoll.addReaction("👎").queue();

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    int yesVotes = 0;
                    int noVotes = 0;
                    for (MessageReaction mr : fullSkipPoll.getReactions()){
                        if (mr.getReactionEmote().isEmoji() && mr.getReactionEmote().getEmoji().equals("\uD83D\uDC4D")){
                            yesVotes = mr.getCount();
                        } else if (mr.getReactionEmote().isEmoji() && mr.getReactionEmote().getEmoji().equals("👎")){
                            noVotes = mr.getCount();
                        }
                        if (yesVotes > 0 && noVotes > 0) break;
                    }

                    boolean success = yesVotes > noVotes;

                    if (success){
                        Utils.sendPM(leader.getUser(), "The fullskip vote has passed!");
                        fullSkipPoll.editMessage(new EmbedBuilder(fullSkipPoll.getEmbeds().get(0)).setDescription("The fullskip vote has passed!")
                                .setColor(Color.green)
                                .addField("👍", String.valueOf(yesVotes), true)
                                .addField("👎", String.valueOf(noVotes), true).build()).queue();
                    } else {
                        Utils.sendPM(leader.getUser(), "The fullskip vote has failed!");
                        fullSkipPoll.editMessage(new EmbedBuilder(fullSkipPoll.getEmbeds().get(0)).setDescription("The fullskip vote has failed!")
                                .setColor(Color.red)
                                .addField("👍", String.valueOf(yesVotes), true)
                                .addField("👎", String.valueOf(noVotes), true).build()).queue();
                    }

                    fullSkipPoll.delete().queueAfter(2, TimeUnit.MINUTES);
                }
            }, TimeUnit.MINUTES.toMillis(minutes));
        } else {
            fullSkip();
        }

    }

    private void fullSkip(){
        isFullSkipped = true;
        raidMsg.editMessage(raidMsg.getContentRaw()).embed(new EmbedBuilder(raidMsg.getEmbeds().get(0)).setTitle("THIS RAID IS A FULL SKIP").build()).queue();
    }

    //changes the Voice Channel cap for the Raid Room. Used when -raid <size> is used
    public void setVoiceCap(int size){
        if (size > 0)
            raidRoom.getManager().setUserLimit(Math.min(size, 99)).queue();
        else
            raidRoom.getManager().setUserLimit(0).queue();
    }

    public void setTimer(int time){
        this.countdownTime = time;
        this.countdown = time;
    }

    private void ping() {
        Message msg = Utils.sendMessage(NestBot.getGuild().getTextChannelById(raidType.getRaidChannelId()),
                "@here " + getLeader().getEffectiveName() + " has started a raid!");
        msg.delete().queue();
    }

    //Emotes that will be added as Reaction for each Raid Type. (e.g. EVENT_RAID has other Reactions than DEFAULT_RAID)
    public static Emote[] defaulRaidEmotes = new Emote[]{
        Emote.NEST,
        Emote.NEST_KEY,
        Emote.QOT,
        Emote.PRIEST,
        Emote.WARRIOR,
        Emote.PALLY,
        Emote.KNIGHT,
        Emote.MYSTIC,
        Emote.PURI,
        Emote.SLOW,
        Emote.NITRO
    };
    public static Emote[] wrRaidEmotes = new Emote[]{
        Emote.NEST,
        Emote.NEST_KEY,
        Emote.QOT,
        Emote.PRIEST,
        Emote.WARRIOR,
        Emote.PALLY,
        Emote.KNIGHT,
        Emote.MYSTIC,
        Emote.PURI,
        Emote.SLOW
    };
    public static Emote[] eventRaidEmotes = new Emote[]{
        Emote.DUNGEON,
        Emote.EVENT_KEY,
        Emote.NITRO,
        Emote.PRIEST,
        Emote.WARRIOR,
        Emote.PALLY,
        Emote.KNIGHT,
        Emote.PURI
    };

}
