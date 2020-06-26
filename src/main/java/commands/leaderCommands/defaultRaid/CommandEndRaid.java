package commands.leaderCommands.defaultRaid;

import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import utils.Utils;
import commands.Command;
import raids.RaidHub;
import org.apache.commons.lang3.StringUtils;


public class CommandEndRaid extends Command {
    public CommandEndRaid() {
        setAliases(new String[] {"end", "finish", "complete", "done", "cleared", "endraid"});
        setMinRank(Rank.ALMOST_RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
        if (RaidHub.getRaid(msg.getMember()) == null) {
            Utils.sendPM(msg.getAuthor(), "You have not started a raid!");
            return;
        }

        if (!RaidHub.getRaid(msg.getMember()).isRaidActive()) {
            RaidHub.getRaid(msg.getMember()).endRaid();
            return;
        }

        if (RaidHub.getRaid(msg.getMember()).isFailed()) {
            RaidHub.getRaid(msg.getMember()).endRaid();
            return;
        }

/*
        if (args.length > 0 && StringUtils.isNumeric(args[0])) {
            RaidHub.getRaid(msg.getMember()).completeRaid(false, Integer.parseInt(args[0]));
            return;
        }
*/

        RaidHub.getRaid(msg.getMember()).completeRaid(false, -1);
    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: End raid");
        embedBuilder.addField("Required rank", "Almost raid leader.", false);
        embedBuilder.addField("Syntax", "-alias <Players remaining>", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Ends a raid and closes the raid room also logs who was left alive.", false);
        return embedBuilder;
}
}
