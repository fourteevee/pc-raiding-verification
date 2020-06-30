package commands.adminCommands;

import commands.Command;
import main.Constants;
import main.NestBot;
import main.Rank;
import main.StatsJson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import punishment.BlacklistManager;
import utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandQuota extends Command {
    public CommandQuota() {
        setAliases(new String[] {"quota"});
        setMinRank(Rank.ALMOST_RL);
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

        EmbedBuilder embedBuilder = new EmbedBuilder();
        long rl_quota = StatsJson.getQuota(member.getId(), "RL_QUOTA");
        long exrl_quota = StatsJson.getQuota(member.getId(), "EXRL_QUOTA");
        long arl_quota = StatsJson.getQuota(member.getId(), "ARL_QUOTA");
        embedBuilder.setTitle(member.getEffectiveName() + "'s run quota")
                .addField("QUOTA", String.valueOf(Math.max(Math.max(rl_quota, exrl_quota), arl_quota)), false);

        Utils.sendEmbed(msg.getTextChannel(), embedBuilder);

    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Quota");
        embedBuilder.addField("Required rank", "ARL", false);
        embedBuilder.addField("Syntax", "-alias @user", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Gets the run quotas of a specific user.", false);
        return embedBuilder;
    }
}
