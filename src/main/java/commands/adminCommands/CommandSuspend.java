package commands.adminCommands;

import commands.Command;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import punishment.Suspension;
import punishment.SuspensionHub;
import utils.Utils;

import java.util.Arrays;

/**
 * Command that suspends a discord user from the raids.
 * Created by MistaCat 10/9/2018
 */
public class CommandSuspend extends Command {
    public CommandSuspend() {
        setAliases(new String[] {"suspend"});
        setMinRank(Rank.RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
        if (args.length < 2) {
            Utils.sendPM(msg.getAuthor(), "Not enough arguments to suspend!");
            return;
        }

        if (msg.getMentionedMembers().isEmpty()) {
            Utils.sendPM(msg.getAuthor(), "Could not find that user!");
            return;
        }

        if (SuspensionHub.getUserSuspension(msg.getMentionedMembers().get(0).getUser()) != null) {
            Utils.sendPM(msg.getAuthor(), "That user is already suspended!");
            return;
        }

        User recipient = msg.getMentionedMembers().get(0).getUser();
        Long time = Utils.fromInputFuture(args[1]);
        String reason = (args.length >= 3) ? Utils.singleArg(Arrays.copyOfRange(args, 2, args.length)) : "Reason no specified";
        SuspensionHub.activeSuspensions.add(new Suspension(msg.getAuthor(), recipient, time, reason));
    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Suspend");
        embedBuilder.addField("Required rank", "raids.Raid leader", false);
        embedBuilder.addField("Syntax", "-alias [@user] [#(s/h/d)], <reason>", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Suspends a discord user from raiding for a specific amount of time", false);
        return embedBuilder;
}
}
