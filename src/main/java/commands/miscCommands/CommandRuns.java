package commands.miscCommands;

import commands.Command;
import main.NestBot;
import main.Rank;
import main.StatsJson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.StringUtils;
import utils.Utils;


public class CommandRuns extends Command {
    public CommandRuns() {
        setAliases(new String[] {"runs"});
        setMinRank(Rank.DEFAULT);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
        if (args.length != 1 || StringUtils.getDigits(args[0]).isEmpty()) {
            Utils.sendPM(msg.getAuthor(), "Please use -alias <@user/userid>");
            return;
        }

        String userId = StringUtils.getDigits(args[0]);

        if (!StatsJson.containsUser(userId)){
            msg.getTextChannel().sendMessage("**User " + args[0] + " doesn't have any runs yet!**").queue();
            return;
        }

        msg.getTextChannel().sendMessage("**User " + args[0] + " has " + StatsJson.getCompletedRaids(userId) + " completed runs!**").queue();

    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Completed Runs");
        embedBuilder.addField("Required rank", "DEFAULT", false);
        embedBuilder.addField("Syntax", "-alias <@user/userId>", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Shows how many runs a user has participated in.", false);
        return embedBuilder;
}
}
