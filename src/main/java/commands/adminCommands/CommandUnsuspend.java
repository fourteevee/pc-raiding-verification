package commands.adminCommands;

import commands.Command;
import main.Constants;
import main.NestBot;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import punishment.SuspensionHub;
import utils.Utils;

/**
 * Command that unsuspends discord users from the raids.
 * Created by MistaCat 10/9/2018
 */
public class CommandUnsuspend extends Command {
    public CommandUnsuspend() {
        setAliases(new String[] {"unsuspend"});
        setMinRank(Rank.RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
        if (args.length != 1 && msg.getMentionedMembers().isEmpty()) {
            Utils.sendPM(msg.getAuthor(), "Improper arguments!");
            return;
        }

        User recipient = msg.getMentionedMembers().get(0).getUser();
        Member recipientMember = msg.getMentionedMembers().get(0);

        Role suspendedRole = NestBot.getGuild().getRoleById(Constants.SUSPENDED);
        Role verifiedRole = NestBot.getGuild().getRoleById(Constants.VERIFIED);

        if (SuspensionHub.getUserSuspension(recipient) == null &&
                !recipientMember.getRoles().contains(suspendedRole)){
            Utils.sendPM(msg.getAuthor(), "That user is not suspended!");
            return;
        }

        if (SuspensionHub.getUserSuspension(recipient) == null &&
                recipientMember.getRoles().contains(NestBot.getGuild().getRoleById(Constants.SUSPENDED))) {

            NestBot.getGuild().removeRoleFromMember(recipientMember, suspendedRole);
            NestBot.getGuild().addRoleToMember(recipientMember, verifiedRole);

            EmbedBuilder susMsg = new EmbedBuilder();
            susMsg.setTitle("You have been unsuspended!");
            susMsg.setDescription("Please follow the rules and have a good day!");
            Utils.sendPMEmbed(recipient, susMsg);

            susMsg = new EmbedBuilder();
            susMsg.setTitle(recipient.getName() + " has been unsuspended!");
            susMsg.setDescription("Unsuspended by: " + msg.getAuthor());
            Utils.sendEmbed(NestBot.getGuild().getTextChannelById(Constants.SUSPENSION_LOGS), susMsg);
            return;
        }

        SuspensionHub.getUserSuspension(recipient).finishSuspension();
    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Unsuspend");
        embedBuilder.addField("Required rank", "raids.Raid leader", false);
        embedBuilder.addField("Syntax", "-alias [@user]", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Unsuspends a discord user so they may partake in raids again.", false);
        return embedBuilder;
}
}
