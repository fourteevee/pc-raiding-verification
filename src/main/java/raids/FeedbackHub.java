package raids;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.Utils;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Listens to the feedback channels that are active
 * Created by MistaCat 10/8/2018
 */
public class FeedbackHub extends ListenerAdapter {
    public static List<LeaderFeedback> activeFeedback = new CopyOnWriteArrayList<>();

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (!isFeedbackComment(event.getMessage()))
            return;

        LeaderFeedback feedback = getFeedbackChannel(event.getMessage());
        if (feedback.getRaiders().contains(event.getMember())) {
            Utils.updateChannelPerms(feedback.getFeedbackRoom(), event.getMember(), EnumSet.of(Permission.MESSAGE_READ, Permission.MESSAGE_ADD_REACTION), EnumSet.of(Permission.MESSAGE_WRITE));
            Utils.sendPM(event.getAuthor(), "Thank you for the feedback! It is appreciated.");
            return;
        }

        event.getMessage().delete();
    }


    /**
     * Gets a Leader Feedback from a message.
     * @param msg
     * @return
     */
    public static LeaderFeedback getFeedbackChannel(Message msg) {
        for (LeaderFeedback feedback : activeFeedback)
            if (feedback.getFeedbackRoom() == msg.getChannel())
                return feedback;

        return null;
    }

    /**
     * Returns a boolean if a messages is a comment in a feedback channel.
     * @param msg
     * @return
     */
    public static boolean isFeedbackComment(Message msg) {
        return getFeedbackChannel(msg) != null;
    }
}
