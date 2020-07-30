package commands.miscCommands;

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

public class CommandPromote extends Command {
    public CommandPromote() {
        setAliases(new String[] {"promote","toarl"});
        setMinRank(Rank.EX_RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
        if (args.length != 1) {
            Utils.sendPM(msg.getAuthor(), "Invalid amount of arguments!");
            return;
        }
        if (msg.getMentionedMembers().isEmpty()) {
            Utils.sendPM(msg.getAuthor(), "Please mention a user to promote.");
            return;
        }

        Member member = msg.getMentionedMembers().get(0);
        if (NestBot.getGuild().getMembersWithRoles(Rank.TRIAL_RL.getRole()).contains(member)) {
            NestBot.getGuild().removeRoleFromMember(member.getId(), NestBot.getGuild().getRoleById(Constants.TRIAL_RL)).queue();
            String ARLprefix = StringUtils.replace(member.getEffectiveName(), ")", "()");
            NestBot.getGuild().modifyNickname(member, ARLprefix).queue();
            NestBot.getGuild().addRoleToMember(member.getId(), NestBot.getGuild().getRoleById(Constants.ALMOST_RL)).queue();

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(member.getEffectiveName() + " has been promoted to ARL!")
                    .setColor(new Color(255, 179, 0));
            Utils.sendEmbed(msg.getTextChannel(), embedBuilder);

        } else {
            Utils.sendPM(msg.getAuthor(), "The user must be a Trial Raid Leader");
        }

    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Promote");
        embedBuilder.addField("Required rank", "Ex. Raid Leader", false);
        embedBuilder.addField("Syntax", "-alias <@user/userId>", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Promotes a TRL to an ARL", false);
        return embedBuilder;
    }
}
