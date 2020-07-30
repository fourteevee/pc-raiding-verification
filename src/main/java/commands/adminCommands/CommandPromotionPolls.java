package commands.adminCommands;

import commands.Command;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class CommandPromotionPolls extends Command {
    public CommandPromotionPolls() {
        setAliases(new String[] {"promotionpoll", "promotionvote"});
        setMinRank(Rank.HEAD_RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {

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
        embedBuilder.addField("Information", "Creates a promotion poll in the channel the command was executed in.", false);
        return embedBuilder;
    }
}
