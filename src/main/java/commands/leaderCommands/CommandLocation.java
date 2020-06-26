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
public class CommandLocation extends Command {
    public CommandLocation() {
        setAliases(new String[] {"location", "loc"});
        setMinRank(Rank.ALMOST_RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
        if (RaidHub.isLeading(msg.getMember())) {

            if (args.length == 1 && StringUtils.isAlphanumeric(args[0])) {
                RaidHub.getRaid(msg.getMember()).setLocation(args[0], true);
                return;
            } else {
                Utils.sendPM(msg.getAuthor(), "Location needs to be an alphanumeric value!");
            }


        }
    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Create raid");
        embedBuilder.addField("Required rank", "Almost raid leader.", false);
        embedBuilder.addField("Syntax", "-alias <room size> <location> <countdown time (seconds)>", false);
        embedBuilder.addField("Syntax", "-alias <Players remaining>", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Creates a new raid room and pings people that a nest raid will be starting.\n" +
                "Can also re-open the room for another raid after one has begun with logging.", false);
        return embedBuilder;
}
}
