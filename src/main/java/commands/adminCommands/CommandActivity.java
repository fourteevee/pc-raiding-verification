package commands.adminCommands;

import commands.Command;
import main.Config;
import main.NestBot;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.StringUtils;
import utils.Utils;


public class CommandActivity extends Command {
    public CommandActivity() {
        setAliases(new String[] {"activity"});
        setMinRank(Rank.OWNER);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {

        if (args.length < 2) {
            Utils.sendPM(msg.getAuthor(), "Invalid amount of arguments!");
            return;
        }

        String activity = args[0];
        String activityMessage = StringUtils.split(msg.getContentRaw(), " ", 3)[2];

        switch (activity.toLowerCase()){
            case "listening":
                NestBot.jda.getPresence().setPresence(Activity.listening(activityMessage), false);
                Config.setActivity(activity + " " + activityMessage);
                break;
            case "streaming":
                if (args.length < 3) {
                    Utils.sendPM(msg.getAuthor(), "Invalid amount of arguments! Please use [stream url] [activity message]");
                    return;
                }
                String streamUrl = args[1];
                activityMessage = StringUtils.split(msg.getContentRaw(), " ", 4)[3];

                if (!Activity.isValidStreamingUrl(streamUrl)){
                    Utils.sendPM(msg.getAuthor(), "Invalid stream url!");
                    return;
                }

                NestBot.jda.getPresence().setPresence(Activity.streaming(activityMessage, streamUrl), false);
                Config.setActivity(activity + " " + streamUrl + " " + activityMessage);
                break;
            case "watching":
                NestBot.jda.getPresence().setPresence(Activity.watching(activityMessage), false);
                Config.setActivity(activity + " " + activityMessage);
                break;
            case "playing":
                NestBot.jda.getPresence().setPresence(Activity.playing(activityMessage), false);
                Config.setActivity(activity + " " + activityMessage);
                break;
            default:
                Utils.sendPM(msg.getAuthor(), "Invalid activity! [listening, streaming, watching, playing]");
                break;
        }
    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Activity");
        embedBuilder.addField("Required rank", "Owner", false);
        embedBuilder.addField("Syntax", "-alias [activity] [activity message]", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Sets the current activity for the bot.", false);
        return embedBuilder;
    }
}
