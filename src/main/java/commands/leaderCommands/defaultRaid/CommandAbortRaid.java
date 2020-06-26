package commands.leaderCommands.defaultRaid;

import commands.Command;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import raids.RaidHub;
import utils.Utils;

public class CommandAbortRaid extends Command {
    public CommandAbortRaid() {
        setAliases(new String[] {"abortraid", "abort", "failure", "mission_failed_we'll_get_em_next_time"});
        setMinRank(Rank.ALMOST_RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
        if (RaidHub.getRaid(msg.getMember()) == null) {
            Utils.sendPM(msg.getAuthor(), "You have not started a raid!");
            return;
        }

        if (!RaidHub.getRaid(msg.getMember()).isRaidActive()) {
            Utils.sendPM(msg.getAuthor(), "Your current raid is not in progress! So you can't abort it!");
            return;
        }

        RaidHub.getRaid(msg.getMember()).abortRaid();
    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Abort raid");
        embedBuilder.addField("Required rank", "Almost raid leader.", false);
        embedBuilder.addField("Syntax", "-alias", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Aborts a failed raid so raid leader may start a new one or end without logging.", false);
        return embedBuilder;
}
}
