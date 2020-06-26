package commands.leaderCommands.defaultRaid;

import commands.Command;
import commands.leaderCommands.HeadCountHub;
import main.Constants;
import main.NestBot;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import utils.Utils;

/**
 * Command to start a nest run. Only usable by raid leaders
 * Created by MistaCat 10/7/2018
 */
public class CommandEndHeadcount extends Command {
    public CommandEndHeadcount() {
        setAliases(new String[] {"endhc"});
        setMinRank(Rank.ALMOST_RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {

        Member member = msg.getMember();

        if (!HeadCountHub.hcMembers.containsKey(member)) {
            Utils.sendPM(member.getUser(), "You don't have an ongoing Head Count!");
            return;
        }

        Message message = HeadCountHub.hcMembers.get(member);
        message.delete().submit();

        HeadCountHub.hcMembers.remove(member);

        Utils.sendPM(member.getUser(), "You've stopped your Head Count");
    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Stop your Head Count");
        embedBuilder.addField("Required rank", "Almost raid leader.", false);
        embedBuilder.addField("Syntax", "-alias", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Stop your ongoing Head Count.", false);
        return embedBuilder;
}
}
