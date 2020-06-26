package commands.leaderCommands.defaultRaid;

import net.dv8tion.jda.api.entities.Message;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import raids.RaidHub;
import utils.Utils;
import commands.Command;


public class CommandBeginRaid extends Command {

    public CommandBeginRaid() {
        setAliases(new String[] {"start", "begin", "beginraid"});
        setMinRank(Rank.ALMOST_RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
        if (!RaidHub.isLeading(msg.getMember())) {
            Utils.sendPM(msg.getAuthor(), "You have not started a raid!");
            return;
        }

        RaidHub.getRaid(msg.getMember()).startRaid();
    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Begin raid");
        embedBuilder.addField("Required rank", "Almost raid leader.", false);
        embedBuilder.addField("Syntax", "-alias", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Closes raid room to begin a nest raid.", false);
        return embedBuilder;
    }
}
