package moderation;

import commands.Command;
import main.NestBot;
import main.PunishmentLogs;
import main.Rank;
import main.StatsJson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import utils.Utils;

public class CommandPunishmentHistory extends Command {
    public CommandPunishmentHistory() {
        setAliases(new String[] {"history","punishmenthistory"});
        setMinRank(Rank.RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
        if (args.length != 1) {
            Utils.sendPM(msg.getAuthor(), "Invalid amount of arguments!");
            return;
        }

        if (msg.getMentionedMembers().isEmpty()) {
            Utils.sendPM(msg.getAuthor(), "Please mention a user");
            return;
        }

        Member member = msg.getMentionedMembers().get(0);

        String suspensions = PunishmentLogs.getPunishments(member.getId(), "suspension");

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.addField("SUSPENSIONS", suspensions, false);
        Utils.sendEmbed(msg.getTextChannel(), embedBuilder);

    }


    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: PunishmentHistory");
        embedBuilder.addField("Required rank", "ARL", false);
        embedBuilder.addField("Syntax", "-alias @user", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Gets the run quotas of a specific user.", false);
        return embedBuilder;
    }
}
