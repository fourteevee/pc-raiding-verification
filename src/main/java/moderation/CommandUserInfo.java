package moderation;

import commands.Command;
import main.NestBot;
import main.PunishmentLogs;
import main.Rank;
import main.StatsJson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import utils.Utils;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;

public class CommandUserInfo extends Command {
    public CommandUserInfo() {
        setAliases(new String[] {"uinfo","userinfo"});
        setMinRank(Rank.RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
        if (args.length != 1) {
            Utils.sendPM(msg.getAuthor(), "Invalid amount of arguments!");
            return;
        }

        if (msg.getMentionedMembers().isEmpty()) {

            try {
                String memberName = args[0];
                List<Member> members =  NestBot.getGuild().getMembersByNickname(memberName, true);

                if (members.size() == 0) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                            embedBuilder.setTitle("Error: " +"`"+ args[0] + " is not a user`")
                            .setColor(new Color(255,179,0))
                            .setFooter("© Pest Control Administration", "https://cdn.discordapp.com/icons/514788290809954305/b5fd5a35617a751d9860116c7a433a58.webp?size=1024")
                            .setTimestamp(new Date().toInstant());
                    Utils.sendEmbed(msg.getTextChannel(), embedBuilder);
                }

                for (Member member : members) {
                    String suspensions = PunishmentLogs.getPunishments(member.getId(), "suspension");
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setThumbnail(member.getUser().getAvatarUrl())
                            .setTitle("**" + member.getEffectiveName() + "'s User Information**")
                            .setColor(new Color(255,179,0))
                            .addField("Name: ", member.getEffectiveName(), false)
                            .addField("ID: ", member.getId(), false)
                            .addField("Joined: ", String.valueOf(member.getTimeJoined().getYear() + "-" + member.getTimeJoined().getMonth() + "-" + member.getTimeJoined().getDayOfMonth()), false)
                            .addField("Highest Role:", String.valueOf(Rank.getHighestRank(member)), false)
                            .addField("Completed Runs:", String.valueOf(StatsJson.getCompletedRaids(member.getId())), false)
                            .addField("Suspensions:", suspensions, false)
                            .setFooter("© Pest Control Administration", "https://cdn.discordapp.com/icons/514788290809954305/b5fd5a35617a751d9860116c7a433a58.webp?size=1024")
                            .setTimestamp(new Date().toInstant());
                    Utils.sendEmbed(msg.getTextChannel(), embedBuilder);

                    }
                } catch (Exception e) {
                Utils.sendPM(msg.getAuthor(), "That is not a valid user");
            }
            return;

        }

        Member member = msg.getMentionedMembers().get(0);

        String suspensions = PunishmentLogs.getPunishments(member.getId(), "suspension");

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(member.getUser().getAvatarUrl())
                .setTitle("**" + member.getEffectiveName() + "'s User Information**")
                .setColor(new Color(255,179,0))
                .addField("Name: ", member.getEffectiveName(), false)
                .addField("ID: ", member.getId(), false)
                .addField("Joined: ", String.valueOf(member.getTimeJoined().getYear() + "-" + member.getTimeJoined().getMonth() + "-" + member.getTimeJoined().getDayOfMonth()), false)
                .addField("Highest Role:", String.valueOf(Rank.getHighestRank(member)), false)
                .addField("Completed Runs:", String.valueOf(StatsJson.getCompletedRaids(member.getId())), false)
                .addField("Suspensions:", suspensions, false)
                .setFooter("© Pest Control Administration", "https://cdn.discordapp.com/icons/514788290809954305/b5fd5a35617a751d9860116c7a433a58.webp?size=1024")
                .setTimestamp(new Date().toInstant());
        Utils.sendEmbed(msg.getTextChannel(), embedBuilder);

    }


    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: UserInfo");
        embedBuilder.addField("Required rank", "EXRL", false);
        embedBuilder.addField("Syntax", "-alias @user", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Gets the user info of a specific user.", false);
        return embedBuilder;
    }
}
