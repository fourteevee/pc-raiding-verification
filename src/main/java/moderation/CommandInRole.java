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
import java.util.Date;
import java.util.List;

public class CommandInRole extends Command {
    public CommandInRole() {
        setAliases(new String[] {"inrole"});
        setMinRank(Rank.RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {

        String roleName = args[0];
        if (args.length != 1) {
            roleName = "";
            for (int i = 0; i < args.length; i++) {
                if (i == args.length - 1) {
                    roleName += args[i];
                    break;
                }
                roleName += args[i] + " ";
            }
        }

        try {
            List<Role> role =  NestBot.getGuild().getRolesByName(roleName, true);
            String membersInRole = "";
            for (Member member : NestBot.getGuild().getMembersWithRoles(role)) {
                if ((membersInRole.length() + member.getNickname().length()) >= 1500) {
                    break;
                } else {
                    membersInRole += member.getNickname() + "\n";
                }
            }
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("**Users in " + roleName.toUpperCase() + "**")
                    .setColor(new Color(255, 179, 0))
                    .addField("Members: ", membersInRole, false)
                    .setFooter("© Pest Control Administration", "https://cdn.discordapp.com/icons/514788290809954305/b5fd5a35617a751d9860116c7a433a58.webp?size=1024")
                    .setTimestamp(new Date().toInstant());
            Utils.sendEmbed(msg.getTextChannel(), embedBuilder);
        } catch (Exception e) {
            Utils.sendPM(msg.getAuthor(), "This is not a valid role");
            return;
        }


    }



    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: InRole");
        embedBuilder.addField("Required rank", "RL", false);
        embedBuilder.addField("Syntax", "-alias @user", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Gets the members of a role.", false);
        return embedBuilder;
    }
}
