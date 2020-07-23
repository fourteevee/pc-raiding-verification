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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private ArrayList<Member> assistReactions = new ArrayList<>();

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
        this.isFullSkipped = false;
        this.raidActive = false;
        raidMsg.delete().queue();

        //reset the lists of users that reacted to get location so that again 3 people can react and get location
        nestKeyReactions = new ArrayList<>();
        eventKeyReactions = new ArrayList<>();
        knightReactions = new ArrayList<>();
        mysticReactions = new ArrayList<>();
        puriReactions = new ArrayList<>();
        nitroReactions = new ArrayList<>();
        assistReactions = new ArrayList<>();


        countdown = countdownTime;
        raidRoom.getManager().setName(raidType.equals(RaidType.EVENT_RAID) ? "Event starting soon!" : "Nest starting soon!").queue();
        this.raidMsg = Utils.sendMessage(NestBot.getGuild().getTextChannelById(raidType.getRaidChannelId()), "A new raid has begun, @here");

        raidMsg = raidMsg.editMessage(raidMsg.getContentRaw()).embed(createRaidMsg().build()).complete();

        Role eligibleRole = NestBot.getGuild().getRoleById(Constants.VERIFIED);

        if (this.raidType.equals(RaidType.EXRL_RAID)) {
            eligibleRole = NestBot.getGuild().getRoleById(Constants.EXTERMINATOR);
        } else if (this.raidType.equals(RaidType.WR_RAID)) {
            eligibleRole = NestBot.getGuild().getRoleById(Constants.WR_RAIDER);
        } else if (this.raidType.equals(RaidType.EVENT_RAID)) {
            eligibleRole = NestBot.getGuild().getRoleById(Constants.EVENT_RAIDER);
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

        StatsJson.incrementQuota(leader.getId(), quota, 1L);
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

        logRaid("aborted");

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
        logRaid("success");

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

    /**
     * Status: success, aborted
     * @param status
     */
    public void logRaid(String status) {
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
        if (status.equalsIgnoreCase("success")){
            embedBuilder.setTitle(leader.getEffectiveName() + " completed a raid!");
        } else if (status.equalsIgnoreCase("aborted")){
            embedBuilder.setTitle(leader.getEffectiveName() + " aborted a raid!");
        }
        embedBuilder.setDescription("Date Completed: " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) +
                "\nTime Taken: " + Utils.formatTimeFull(System.currentTimeMillis() - startTime) +
                "\nPlayers remaining in run (" + getRaidRoom().getMembers().size() + "): " + playerNames);

        String nestUsers = "";
        List<User> nestReactions = raidMsg.retrieveReactionUsers(Emote.NEST.getEmote()).complete();
        for (int i = 0; i < nestReactions.size(); i++) {
            User user = nestReactions.get(i);
            if (!user.isBot()) {
                if ((nestUsers.length() + user.getAsMention().length()) >= 1000){
                    nestUsers += "And " + (nestReactions.size() - i - 1) + " others...";
                } else {
                    nestUsers += user.getAsMention() + "\n";
                }
            }
        }
        String nestKeyUsers = "";
        List<User> nestKeyReactions = raidMsg.retrieveReactionUsers(Emote.NEST_KEY.getEmote()).complete();
        for (int i = 0; i < nestKeyReactions.size(); i++) {
            User user = nestKeyReactions.get(i);
            if (!user.isBot()) {
                if ((nestKeyUsers.length() + user.getAsMention().length()) >= 1000){
                    nestKeyUsers += "And " + (nestKeyReactions.size() - i - 1) + " others...";
                } else {
                    nestKeyUsers += user.getAsMention() + "\n";
                }
            }
        }
        String qotUsers = "";
        List<User> qotReactions = raidMsg.retrieveReactionUsers(Emote.QOT.getEmote()).complete();
        for (int i = 0; i < qotReactions.size(); i++) {
            User user = qotReactions.get(i);
            if (!user.isBot()) {
                if ((qotUsers.length() + user.getAsMention().length()) >= 1000){
                    qotUsers += "And " + (qotReactions.size() - i - 1) + " others...";
                } else {
                    qotUsers += user.getAsMention() + "\n";
                }
            }
        }
        String priestUsers = "";
        List<User> priestReactions = raidMsg.retrieveReactionUsers(Emote.PRIEST.getEmote()).complete();
        for (int i = 0; i < priestReactions.size(); i++) {
            User user = priestReactions.get(i);
            if (!user.isBot()) {
                if ((priestUsers.length() + user.getAsMention().length()) >= 1000){
                    priestUsers += "And " + (priestReactions.size() - i - 1) + " others...";
                } else {
                    priestUsers += user.getAsMention() + "\n";
                }
            }
        }
        String warriorUsers = "";
        List<User> warriorReactions = raidMsg.retrieveReactionUsers(Emote.WARRIOR.getEmote()).complete();
        for (int i = 0; i < warriorReactions.size(); i++) {
            User user = warriorReactions.get(i);
            if (!user.isBot()) {
                if ((warriorUsers.length() + user.getAsMention().length()) >= 1000){
                    warriorUsers += "And " + (warriorReactions.size() - i - 1) + " others...";
                } else {
                    warriorUsers += user.getAsMention() + "\n";
                }
            }
        }
        String pallyUsers = "";
        List<User> pallyReactions = raidMsg.retrieveReactionUsers(Emote.PALLY.getEmote()).complete();
        for (int i = 0; i < pallyReactions.size(); i++) {
            User user = pallyReactions.get(i);
            if (!user.isBot()) {
                if ((pallyUsers.length() + user.getAsMention().length()) >= 1000){
                    pallyUsers += "And " + (pallyReactions.size() - i - 1) + " others...";
                } else {
                    pallyUsers += user.getAsMention() + "\n";
                }
            }
        }
        String knightUsers = "";
        List<User> knightReactions = raidMsg.retrieveReactionUsers(Emote.KNIGHT.getEmote()).complete();
        for (int i = 0; i < knightReactions.size(); i++) {
            User user = knightReactions.get(i);
            if (!user.isBot()) {
                if ((knightUsers.length() + user.getAsMention().length()) >= 1000){
                    knightUsers += "And " + (knightReactions.size() - i - 1) + " others...";
                } else {
                    knightUsers += user.getAsMention() + "\n";
                }
            }
        }
        String mysticUsers = "";
        List<User> mysticReactions = raidMsg.retrieveReactionUsers(Emote.MYSTIC.getEmote()).complete();
        for (int i = 0; i < mysticReactions.size(); i++) {
            User user = mysticReactions.get(i);
            if (!user.isBot()) {
                if ((mysticUsers.length() + user.getAsMention().length()) >= 1000){
                    mysticUsers += "And " + (mysticReactions.size() - i - 1) + " others...";
                } else {
                    mysticUsers += user.getAsMention() + "\n";
                }
            }
        }
        String slowUsers = "";
        List<User> slowReactions = raidMsg.retrieveReactionUsers(Emote.SLOW.getEmote()).complete();
        for (int i = 0; i < slowReactions.size(); i++) {
            User user = slowReactions.get(i);
            if (!user.isBot()) {
                if ((slowUsers.length() + user.getAsMention().length()) >= 1000){
                    slowUsers += "And " + (slowReactions.size() - i - 1) + " others...";
                } else {
                    slowUsers += user.getAsMention() + "\n";
                }
            }
        }
        String nitroUsers = "";
        List<User> nitroReactions = raidMsg.retrieveReactionUsers(Emote.NITRO.getEmote()).complete();
        for (int i = 0; i < nitroReactions.size(); i++) {
            User user = nitroReactions.get(i);
            if (!user.isBot()) {
                if ((nitroUsers.length() + user.getAsMention().length()) >= 1000){
                    nitroUsers += "And " + (nitroReactions.size() - i - 1) + " others...";
                } else {
                    nitroUsers += user.getAsMention() + "\n";
                }
            }
        }
        String assistUsers = "";
        List<User> assistReactions = raidMsg.retrieveReactionUsers(Emote.ASSIST.getEmote()).complete();
        for (int i = 0; i < assistReactions.size(); i++) {
            User user = assistReactions.get(i);
            if (!user.isBot()) {
                String Assists = "SEC_ASSISTS";
                    if (NestBot.getGuild().getMembersWithRoles(Rank.OFFICER.getRole()).contains(user)){
                        Assists = "OFFICER_ASSISTS";
                        if (NestBot.getGuild().getMembersWithRoles(Rank.ALMOST_RL.getRole()).contains(user)){
                            Assists = "ARL_ASSISTS";
                        } if (NestBot.getGuild().getMembersWithRoles(Rank.RL.getRole()).contains(user)){
                            Assists = "RL_ASSISTS";
                            if (NestBot.getGuild().getMembersWithRoles(Rank.EX_RL.getRole()).contains(user)){
                                Assists = "EXRL_ASSISTS";
                            }
                        }
                    }
                    StatsJson.incrementAssists(user.getId(), Assists, 1L);
             assistUsers += user.getAsMention() + "\n";
            }
        }
        String dungeonUsers = "";
        List<User> dungeonReactions = raidMsg.retrieveReactionUsers(Emote.DUNGEON.getEmote()).complete();
        for (int i = 0; i < dungeonReactions.size(); i++) {
            User user = dungeonReactions.get(i);
            if (!user.isBot()) {
                if ((dungeonUsers.length() + user.getAsMention().length()) >= 1000){
                    dungeonUsers += "And " + (dungeonReactions.size() - i - 1) + " others...";
                } else {
                    dungeonUsers += user.getAsMention() + "\n";
                }
            }
        }
        String eventKeyUsers = "";
        List<User> eventKeyReactions = raidMsg.retrieveReactionUsers(Emote.EVENT_KEY.getEmote()).complete();
        for (int i = 0; i < eventKeyReactions.size(); i++) {
            User user = eventKeyReactions.get(i);
            if (!user.isBot()) {
                if ((eventKeyUsers.length() + user.getAsMention().length()) >= 1000){
                    eventKeyUsers += "And " + (eventKeyReactions.size() - i - 1) + " others...";
                } else {
                    eventKeyUsers += user.getAsMention() + "\n";
                }
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
        embedBuilder.addField(Emote.ASSIST.display(), assistUsers.isEmpty() ? "No users" : assistUsers, true);
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
            embedBuilder.addField("If you are a leader or security assisting in the run, react with", Emote.ASSIST.display(), true);
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
     * @param notify If true it will notify all users that already reacted and got the *old* location
     */
    public void setLocation(String location, boolean notify) {
        this.location = location;
        if (notify) {
            Stream.of(nestKeyReactions, eventKeyReactions, knightReactions, mysticReactions, puriReactions, nitroReactions, assistReactions)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()).stream().filter(member -> !member.getUser().isBot()).forEach(member -> {
                try {
                    member.getUser().openPrivateChannel().submit().get().sendMessage("A location has been set for " + this.leader.getEffectiveName() + "'s raid: **" + location + "**").submit();
                } catch ( InterruptedException | ExecutionException e ) {
                    e.printStackTrace();
                }
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
                    final Message finalFullSkipPoll = fullSkipPoll.getTextChannel().retrieveMessageById(fullSkipPoll.getId()).complete();
                    for (MessageReaction mr : finalFullSkipPoll.getReactions()){
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
                        finalFullSkipPoll.editMessage(new EmbedBuilder(finalFullSkipPoll.getEmbeds().get(0)).setDescription("The fullskip vote has passed!")
                                .setColor(Color.green)
                                .addField("👍", String.valueOf(yesVotes - 1), true)
                                .addField("👎", String.valueOf(noVotes - 1), true).build()).queue();
                        fullSkip();
                    } else {
                        Utils.sendPM(leader.getUser(), "The fullskip vote has failed!");
                        finalFullSkipPoll.editMessage(new EmbedBuilder(finalFullSkipPoll.getEmbeds().get(0)).setDescription("The fullskip vote has failed!")
                                .setColor(Color.red)
                                .addField("👍", String.valueOf(yesVotes), true)
                                .addField("👎", String.valueOf(noVotes), true).build()).queue();
                    }

                    finalFullSkipPoll.delete().queueAfter(2, TimeUnit.MINUTES);
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
        Emote.NITRO,
        Emote.ASSIST
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
