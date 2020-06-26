package commands.leaderCommands;

import commands.Command;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.StringUtils;
import raids.RaidHub;
import utils.Utils;

/**
 * Command to start a nest run. Only usable by raid leaders
 * Created by MistaCat 10/7/2018
 */
public class CommandFullSkip extends Command {
    public CommandFullSkip() {
        setAliases(new String[] {"fskip"});
        setMinRank(Rank.ALMOST_RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
        if (RaidHub.isLeading(msg.getMember())) {
            if (RaidHub.getRaid(msg.getMember()).isRaidActive()) {
                Utils.sendPM(msg.getAuthor(), "Your current raid is already in progress. So you can't start a fullskip!");
                return;
            }

            if (RaidHub.getRaid(msg.getMember()).isFullSkipped()) {
                Utils.sendPM(msg.getAuthor(), "Your raid is already fullskipped!");
                return;
            }

            if (args.length == 1 && StringUtils.isNumeric(args[0])) {
                RaidHub.getRaid(msg.getMember()).initiateFullSkip(Integer.parseInt(args[0]));
                return;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("force")) {
                RaidHub.getRaid(msg.getMember()).initiateFullSkip(0);
                return;
            } else {
                Utils.sendPM(msg.getAuthor(), "Please set a valid time value or use force.");
            }


        }
    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Full Skip");
        embedBuilder.addField("Required rank", "Almost raid leader.", false);
        embedBuilder.addField("Syntax", "-alias <force>", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Full skips a raid.", false);
        return embedBuilder;
}
}
