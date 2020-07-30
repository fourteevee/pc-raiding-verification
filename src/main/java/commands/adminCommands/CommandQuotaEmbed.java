package commands.adminCommands;

import commands.Command;
import main.Constants;
import main.NestBot;
import main.Rank;
import main.StatsJson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.StringUtils;
import utils.Utils;

import java.awt.*;
import java.util.List;

public class CommandQuotaEmbed extends Command {
    public CommandQuotaEmbed() {
        setAliases(new String[]{"quotaembed"});
        setMinRank(Rank.HEAD_RL);
    }
    public static void updateQuota() {
        List<Message> messages = NestBot.getGuild().getTextChannelById(Constants.RUN_QUOTAS).getHistory().retrievePast(10).complete();
        NestBot.getGuild().getTextChannelById(Constants.RUN_QUOTAS).deleteMessages(messages).queue();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("**Raid Leader and Security Run Quotas                            **");
        Utils.sendEmbed(NestBot.getGuild().getTextChannelById(Constants.RUN_QUOTAS), embedBuilder);

        String ExRaidLeaders = "";
        for (Member member : NestBot.getGuild().getMembersWithRoles(Rank.EX_RL.getRole())) {
            ExRaidLeaders += ("**" + member.getEffectiveName() + "'s run quota: **" + "\n" + "Exterminator Runs: " + String.valueOf(StatsJson.getQuota(member.getId(),"EXRL_QUOTA")) + " | Runs: " + String.valueOf(StatsJson.getQuota(member.getId(),"RL_QUOTA")) + " | Assists: " + String.valueOf(StatsJson.getAllAssists(member.getId())) + "\n");
        }


        String RaidLeaders = "";
        for (Member member : NestBot.getGuild().getMembersWithRoles(Rank.RL.getRole())) {
            if (!NestBot.getGuild().getMembersWithRoles(Rank.EX_RL.getRole()).contains(member)) {
                RaidLeaders += ("**" + member.getEffectiveName() + "'s run quota: **" + "\n" + " Runs: " + String.valueOf(StatsJson.getQuota(member.getId(),"RL_QUOTA")) + " | Assists: " + String.valueOf(StatsJson.getAllAssists(member.getId())) + "\n");
            }
        }

        String AlmostRaidLeaders = "";
        for (Member member : NestBot.getGuild().getMembersWithRoles(Rank.ALMOST_RL.getRole())) {
                AlmostRaidLeaders += ("**" + member.getEffectiveName() + "'s run quota: **" + "\n" + " Runs: " + String.valueOf(StatsJson.getQuota(member.getId(),"ARL_QUOTA")) + " | Assists: " + String.valueOf(StatsJson.getAllAssists(member.getId())) + "\n");
        }


        String Officer = "";
        for (Member member : NestBot.getGuild().getMembersWithRoles(Rank.OFFICER.getRole())) {
            if (!(NestBot.getGuild().getMembersWithRoles(Rank.ALMOST_RL.getRole()).contains(member) || NestBot.getGuild().getMembersWithRoles(Rank.RL.getRole()).contains(member))) {
                Officer += ("**" + member.getEffectiveName() + "'s assist quota: **" + "\n" + "Assists: " + String.valueOf(StatsJson.getAllAssists(member.getId())) + "\n");
            }
        }


        String Security = "";
        for (Member member : NestBot.getGuild().getMembersWithRoles(Rank.SECURITY.getRole())) {
            if (!NestBot.getGuild().getMembersWithRoles(Rank.OFFICER.getRole()).contains(member)) {
                if (!(NestBot.getGuild().getMembersWithRoles(Rank.ALMOST_RL.getRole()).contains(member) || NestBot.getGuild().getMembersWithRoles(Rank.RL.getRole()).contains(member))) {

                    Security += ("** " + member.getEffectiveName() + "'s assist quota: **" + "\n" + "Assists: " + String.valueOf(StatsJson.getAllAssists(member.getId())) + "\n");
                }
            }
        }

        EmbedBuilder embedBuilder2 = new EmbedBuilder();
        embedBuilder2.setThumbnail("https://media.discordapp.net/attachments/714142492303294526/719803366997360660/Pest_Detector.png")
        .addField("**__Exterminator Raid Leaders__**", ExRaidLeaders, false)
        .addField("**__Raid Leaders__**", RaidLeaders, false)
        .addField("**__Almost Raid Leader__**", AlmostRaidLeaders, false)
        .addField("**__Officers__**", Officer, false)
        .addField("**__Securities__**", Security, false);

        Utils.sendEmbed(NestBot.getGuild().getTextChannelById(Constants.RUN_QUOTAS), embedBuilder2);
    }



    @Override
    public void execute(Message msg, String alias, String[] args) {
        updateQuota();
    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: QuotaEmbed");
        embedBuilder.addField("Required rank", "HRL", false);
        embedBuilder.addField("Syntax", "-alias @user", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Creates the quota embed sheet in the run quotas channel.", false);
        return embedBuilder;
    }
}
