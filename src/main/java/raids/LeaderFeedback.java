package raids;

import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import lombok.Getter;
import main.Constants;
import main.NestBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import utils.Utils;

import java.awt.*;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * A leader feedback class that holds all the information for a feedback
 * Created by MistaCat 10/8/2018
 */
@Getter
public class LeaderFeedback {
    private TextChannel feedbackRoom;
    private Message feedbackMsg;
    private Member leader;
    private List<Member> raiders;

    private final ScheduledExecutorService TIMER = new ScheduledThreadPoolExecutor(1);
    private final Long FEEDBACK_TIME = 3L;

    public LeaderFeedback(Member leader, List<Member> raiders) {
        this.leader = leader;
        this.raiders = raiders;

        initializeFeedback();
    }

    private void initializeFeedback() {

        try {
            this.feedbackRoom = NestBot.getGuild().getCategoryById(Constants.FEEDBACK_CATEGORY)
                    .createTextChannel(leader.getEffectiveName().toLowerCase().replace(" ", "-") + "-feedback").submit().get();

            Utils.updateChannelPerms(feedbackRoom, NestBot.getGuild().getPublicRole(), EnumSet.noneOf(Permission.class), EnumSet.of(Permission.MESSAGE_READ));

            for (Member member : raiders)
                Utils.updateChannelPerms(feedbackRoom, member, EnumSet.of(Permission.MESSAGE_READ), EnumSet.noneOf(Permission.class));

            createPoll();

            TIMER.schedule(this::endFeedback, FEEDBACK_TIME, TimeUnit.MINUTES);
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        } catch ( ExecutionException e ) {
            e.printStackTrace();
        }

    }

    private void endFeedback() {
        FeedbackHub.activeFeedback.remove(this);
        TIMER.shutdown();
        logFeedback();

        feedbackRoom.delete().submit();
    }

    private void logFeedback() {
        int pos = 0;
        int neg = 0;

        for (MessageReaction messageReaction : feedbackRoom.retrieveMessageById(feedbackMsg.getId()).complete().getReactions()){
            if (messageReaction.getReactionEmote().isEmoji() && messageReaction.getReactionEmote().getEmoji().equals("\uD83D\uDC4D")){
                pos = messageReaction.retrieveUsers().stream().filter(u -> !u.isBot() && raiders.contains(messageReaction.getGuild().getMember(u))).collect(Collectors.toList()).size();
            } else if (messageReaction.getReactionEmote().isEmoji() && messageReaction.getReactionEmote().getEmoji().equals("\uD83D\uDC4E")){
                neg = messageReaction.retrieveUsers().stream().filter(u -> !u.isBot() && raiders.contains(messageReaction.getGuild().getMember(u))).collect(Collectors.toList()).size();
            }
        }

        EmbedBuilder pollResults = new EmbedBuilder();
        pollResults.setTitle(leader.getEffectiveName() + "'s feedback");
        pollResults.setColor(new Color(0, 255, 0));
        pollResults.setThumbnail(leader.getUser().getAvatarUrl());
        pollResults.setDescription("Positive votes: " + pos +
                "\nNegative votes: " + neg +
                "\nComments: " + feedbackRoom.getHistory().retrievePast(100).complete().stream()
                                    .filter(msg -> !msg.getAuthor().isBot()).collect(Collectors.toList()).size());
        Utils.sendEmbed(NestBot.getGuild().getTextChannelById(Constants.FEEDBACK_LOGS), pollResults);

        for (Message msg : feedbackRoom.getHistory().retrievePast(100).complete().stream().filter(msg -> !msg.getAuthor().isBot()).collect(Collectors.toList())) {
            Utils.sendMessage(NestBot.getGuild().getTextChannelById(Constants.FEEDBACK_LOGS),
                    "**" + msg.getMember().getEffectiveName() + "** " + msg.getAuthor().getAsTag() + " [" + msg.getAuthor().getId() + "] " + " commented: \n" + msg.getContentRaw());
        }
    }

    private void createPoll() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(leader.getEffectiveName() + " feedback!");
        embedBuilder.setThumbnail(leader.getUser().getAvatarUrl());
        embedBuilder.setColor(new Color(0, 255, 0));
        embedBuilder.setDescription("Rate how " + leader.getEffectiveName() + " lead the raid!\nPlease react with a thumbs up or a thumbs down!\n" +
                "After you react please leave a comment below about the raid!");
        this.feedbackMsg = Utils.sendEmbed(feedbackRoom, embedBuilder);
        Utils.addReaction(feedbackMsg, "\uD83D\uDC4D");
        Utils.addReaction(feedbackMsg, "\uD83D\uDC4E");
    }
}
