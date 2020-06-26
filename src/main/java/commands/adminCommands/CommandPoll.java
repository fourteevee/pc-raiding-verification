package commands.adminCommands;

import commands.Command;
import main.Constants;
import main.NestBot;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.apache.commons.lang.math.NumberUtils;
import utils.Utils;

import java.util.concurrent.TimeUnit;

import static main.NestBot.eventWaiter;

public class CommandPoll extends Command {
    public CommandPoll() {
        setAliases(new String[] {"poll", "vote"});
        setMinRank(Rank.HEAD_RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {

        User user = msg.getAuthor();

        if (args.length < 2 || !NumberUtils.isDigits(args[0])) {
            Utils.sendPM(msg.getAuthor(), "Please specify a duration! (minutes)");
            return;
        }

        int duration = Integer.parseInt(args[0]);

        String pollQuestion = msg.getContentRaw().split(" ", 3)[2];

        Message pollMessage = Utils.sendEmbed(msg.getTextChannel(), new EmbedBuilder().setTitle("Poll").setThumbnail(user.getAvatarUrl()).setDescription(pollQuestion)
                .setFooter("👍: Yes  👎: No  🔈: Abstain"));

        pollMessage.addReaction("👍").complete();
        pollMessage.addReaction("👎").complete();
        pollMessage.addReaction("🔈").complete();

        eventWaiter.waitForEvent(GuildMessageReactionAddEvent.class, event -> {
            if (event.getMessageId().equals(pollMessage.getId())){
                if (event.getReactionEmote().isEmoji()) {
                    String emoji = event.getReactionEmote().getEmoji();
                    if (!emoji.equals("👍") && !emoji.equals("👎") && !emoji.equals("🔈")){
                        event.getReaction().removeReaction(event.getUser()).queue();
                    }
                } else {
                    event.getReaction().removeReaction(event.getUser()).queue();
                }
            }
            return false;
        }, event -> {
        }, duration, TimeUnit.MINUTES, () -> {
            Utils.sendEmbed(NestBot.getGuild().getTextChannelById(Constants.VOTE_LOGS), new EmbedBuilder().setTitle("Poll - " + msg.getMember().getEffectiveName())
                    .setThumbnail(user.getAvatarUrl())
                    .setDescription(pollQuestion + "\n\n" +
                            ":thumbsup: - " + (pollMessage.retrieveReactionUsers("👍").complete().size() - 1) + "\n" +
                            ":thumbsdown: - "  + (pollMessage.retrieveReactionUsers("👎").complete().size() - 1) + "\n" +
                            ":mute: - " + (pollMessage.retrieveReactionUsers("🔈").complete().size() - 1)));
            pollMessage.delete().queue();
        });


    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Poll");
        embedBuilder.addField("Required rank", "Head RL", false);
        embedBuilder.addField("Syntax", "-alias [duration (minutes)] [poll question]", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Creates a poll in the channel the command was executed in.", false);
        return embedBuilder;
    }
}
