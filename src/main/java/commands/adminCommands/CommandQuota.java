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

import java.security.Security;
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

         if (Rank.getHighestRank(member).isAtLeast(Rank.SECURITY) && (NestBot.getGuild().getMembersWithRoles(Rank.ALMOST_RL.getRole()).contains(member) || NestBot.getGuild().getMembersWithRoles(Rank.RL.getRole()).contains(member))) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(member.getEffectiveName() + "'s runs quota")
                    .setThumbnail(member.getUser().getAvatarUrl())
                    .addField("QUOTA", String.valueOf(StatsJson.getAllQuota(member.getId())), false);
             if (Rank.getHighestRank(member).isAtLeast(Rank.EX_RL)) {
                 embedBuilder.addField("EXQUOTA", String.valueOf(StatsJson.getQuota(member.getId(),"EXRL_QUOTA")), true);
             }
             embedBuilder.addField("ASSISTS", String.valueOf(StatsJson.getAllAssists(member.getId())), false);
            Utils.sendEmbed(msg.getTextChannel(), embedBuilder);
        }  else if (Rank.getHighestRank(member).isAtLeast(Rank.SECURITY) && !(NestBot.getGuild().getMembersWithRoles(Rank.ALMOST_RL.getRole()).contains(member) || NestBot.getGuild().getMembersWithRoles(Rank.RL.getRole()).contains(member))) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(member.getEffectiveName() + "'s assist quota")
                    .setThumbnail(member.getUser().getAvatarUrl())
                    .addField("ASSISTS", String.valueOf(StatsJson.getAllAssists(member.getId())), false);
            Utils.sendEmbed(msg.getTextChannel(), embedBuilder);
        } else if (Rank.getHighestRank(member).isAtLeast(Rank.ALMOST_RL)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(member.getEffectiveName() + "'s run quota")
                    .setThumbnail(member.getUser().getAvatarUrl())
                    .addField("QUOTA", String.valueOf(StatsJson.getAllQuota(member.getId())), false);
             if (Rank.getHighestRank(member).isAtLeast(Rank.EX_RL)) {
                 embedBuilder.addField("EXQUOTA", String.valueOf(StatsJson.getQuota(member.getId(),"EXRL_QUOTA")), true);
             }
                    embedBuilder.addField("ASSISTS", String.valueOf(StatsJson.getAllAssists(member.getId())), false);

            Utils.sendEmbed(msg.getTextChannel(), embedBuilder);
        }

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
