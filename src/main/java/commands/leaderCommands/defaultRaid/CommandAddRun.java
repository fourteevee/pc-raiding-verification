package commands.leaderCommands.defaultRaid;

import commands.Command;
import main.Rank;
import main.StatsJson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang.math.NumberUtils;
import raids.Raid;
import raids.RaidHub;
import utils.Utils;


public class CommandAddRun extends Command {

    public CommandAddRun() {
        setAliases(new String[] {"addrun"});
        setMinRank(Rank.ALMOST_RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
        if (!RaidHub.isLeading(msg.getMember())) {
            Utils.sendPM(msg.getAuthor(), "You have not started a raid!");
            return;
        }

        Raid raid = RaidHub.getRaid(msg.getMember());
        if (!raid.isRaidActive()) {
            Utils.sendPM(msg.getAuthor(), "Your raid is not active! Use -start first");
            return;
        }

        String quota = "ARL_QUOTA";

        if (msg.getGuild().getMembersWithRoles(Rank.EX_RL.getRole()).contains(msg.getMember())){
            quota = "EXRL_QUOTA";
        } else if (msg.getGuild().getMembersWithRoles(Rank.RL.getRole()).contains(msg.getMember())){
            quota = "RL_QUOTA";
        }

        if (args.length == 1){
            if (NumberUtils.isDigits(args[0])){
                StatsJson.incrementQuota(msg.getMember().getId(), quota, Long.parseLong(args[0]));
                Utils.sendPM(msg.getAuthor(), args[0] + " runs have been added");
                return;
            }
        }

        StatsJson.incrementQuota(msg.getMember().getId(), quota, 1);
        Utils.sendPM(msg.getAuthor(), "Run has been added");
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
