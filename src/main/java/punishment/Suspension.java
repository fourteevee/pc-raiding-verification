package punishment;

import lombok.Getter;
import main.Constants;
import main.PunishmentLogs;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import static main.NestBot.getGuild;

/**
 * A class to hold information about a suspension.
 * Created by MistaCat 10/9/2018
 */
@Getter
public class Suspension {
    private User recipient;
    private User punisher;
    private Long suspensionTime;
    private String reason;
    private String displayDate;

    public Suspension(User punisher, User recipient, Long time, String reason) {
        this.punisher = punisher;
        this.recipient = recipient;
        this.suspensionTime = time;
        this.reason = reason;
        this.displayDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());

        PunishmentLogs.logPunishment(recipient.getId(), "suspension", punisher.getName(), reason, displayDate);
        initializeSuspension();
    }

    private void initializeSuspension() {
        getGuild().removeRoleFromMember(getGuild().getMember(recipient), getGuild().getRoleById(Constants.VERIFIED)).queue();
        getGuild().removeRoleFromMember(getGuild().getMember(recipient), getGuild().getRoleById(Constants.EXTERMINATOR)).queue();
        getGuild().addRoleToMember(getGuild().getMember(recipient), getGuild().getRoleById(Constants.SUSPENDED)).queue();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("You were suspended!");
        embedBuilder.setDescription("Suspended by: " + punisher +
                "\nReason: " + reason +
                "\nExpires in: " + Utils.formatTimeFullFromNow(suspensionTime));
        Utils.sendPMEmbed(recipient, embedBuilder);
        logSuspension(false);
    }

    public void finishSuspension() {
        getGuild().removeRoleFromMember(getGuild().getMember(recipient), getGuild().getRoleById(Constants.SUSPENDED)).queue();
        getGuild().addRoleToMember(getGuild().getMember(recipient), getGuild().getRoleById(Constants.VERIFIED)).queue();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("You have been unsuspended!");
        embedBuilder.setDescription("Please follow the rules and have a good day!");
        Utils.sendPMEmbed(recipient, embedBuilder);
        logSuspension(true);
        SuspensionHub.activeSuspensions.remove(this);
    }

    private void logSuspension(boolean unsuspend) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (unsuspend) {
            embedBuilder.setTitle(recipient.getName() + " has been unsuspended!");
            embedBuilder.setDescription("Date punished: " + displayDate +
                    "\nSuspended by: " + punisher);
        } else {
            embedBuilder.setTitle(recipient.getName() + " has been suspended!");
            embedBuilder.setDescription("Date punished: " + displayDate +
                    "\nSuspended by: " + punisher +
                    "\nReason: " + reason +
                    "\nExpires in: " + Utils.formatTimeFullFromNow(suspensionTime));
        }
        Utils.sendEmbed(getGuild().getTextChannelById(Constants.SUSPENSION_LOGS), embedBuilder);
    }
}
