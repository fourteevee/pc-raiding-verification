package commands.leaderCommands;

import commands.Command;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import raids.Raid;
import raids.RaidHub;
import raids.RaidHub.RaidType;
import utils.Utils;

import java.util.List;


public class CommandEndAllRaids extends Command {
    public CommandEndAllRaids() {
        setAliases(new String[] {"endall", "finishall", "completeall", "doneall", "killraids", "killuserraids", "killex", "killevent", "killwr"});
        setMinRank(Rank.HEAD_RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {

        if (RaidHub.activeRaids.isEmpty()) {
            Utils.sendPM(msg.getAuthor(), "No ongoing raids available!");
            return;
        }

        if (alias.equalsIgnoreCase("killuserraids")){
            try {
                Member member = msg.getGuild().getMemberById(args[0]);
                Raid raid = RaidHub.getRaid(member);
                if (raid != null){
                    raid.completeRaid(false, -1);
                    Utils.sendPM(msg.getAuthor(), "Successfully killed user raid!");
                } else {
                    Utils.sendPM(msg.getAuthor(), "User doesn't have ongoing raids!");
                }
            } catch ( Exception ex ) {
                Utils.sendPM(msg.getAuthor(), "User couldn't be found!");
            }
            return;
        }

        if (alias.equalsIgnoreCase("killex")){
            List<Raid> raids = RaidHub.getRaidsByType(RaidType.EXRL_RAID);
            if (raids.isEmpty()) {
                Utils.sendPM(msg.getAuthor(), "No ongoing ex-raids available!");
                return;
            }

            for (Raid raid : raids){
                raid.completeRaid(false, -1);
            }
            Utils.sendPM(msg.getAuthor(), "Successfully killed all ex-raids!");
            return;
        }

        if (alias.equalsIgnoreCase("killwr")){
            List<Raid> raids = RaidHub.getRaidsByType(RaidType.WR_RAID);
            if (raids.isEmpty()) {
                Utils.sendPM(msg.getAuthor(), "No ongoing wr-raids available!");
                return;
            }

            for (Raid raid : raids){
                raid.completeRaid(false, -1);
            }
            Utils.sendPM(msg.getAuthor(), "Successfully killed all wr-raids!");
            return;
        }

        if (alias.equalsIgnoreCase("killevent")){
            List<Raid> raids = RaidHub.getRaidsByType(RaidType.EVENT_RAID);
            if (raids.isEmpty()) {
                Utils.sendPM(msg.getAuthor(), "No ongoing event-raids available!");
                return;
            }

            for (Raid raid : raids){
                raid.completeRaid(false, -1);
            }
            Utils.sendPM(msg.getAuthor(), "Successfully killed all event-raids!");
            return;
        }

        for (Raid raid : RaidHub.activeRaids){
            raid.completeRaid(false, -1);
        }
        Utils.sendPM(msg.getAuthor(), "Successfully killed all raids!");
    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: End all raids");
        embedBuilder.addField("Required rank", "Head raid leader.", false);
        embedBuilder.addField("Syntax", "-alias", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Ends all ongoing raids.", false);
        return embedBuilder;
}
}
