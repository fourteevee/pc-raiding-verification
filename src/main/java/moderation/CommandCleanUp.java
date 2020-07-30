package moderation;

import commands.Command;
import main.NestBot;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import utils.Utils;

import java.awt.*;

public class CommandCleanUp extends Command {
    public CommandCleanUp() {
        setAliases(new String[] {"cleanup"});
        setMinRank(Rank.SECURITY);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {

        try {
            Integer amount = Integer.parseInt(args[0]);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(new Color(255, 179, 0))
                    .setTitle("Cleaning up " + amount + "messages.");
            Utils.sendEmbed(msg.getTextChannel(), embedBuilder);

            NestBot.getGuild().getTextChannelById(msg.getTextChannel().getId()).deleteMessages(NestBot.getGuild().getTextChannelById(msg.getTextChannel().getId()).getHistory().retrievePast(amount + 2).complete()).complete();
        } catch (Exception e) {
            Utils.sendPM(msg.getAuthor(), "Please specify a number of messages to delete. Example: `-cleanup 5`");
            NestBot.getGuild().getTextChannelById(msg.getTextChannel().getId()).deleteMessageById(msg.getId()).queue();
        }


    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: CleanUp");
        embedBuilder.addField("Required rank", "SECURITY", false);
        embedBuilder.addField("Syntax", "-alias @user", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Cleans up the messages in a channel", false);
        return embedBuilder;
    }

}
