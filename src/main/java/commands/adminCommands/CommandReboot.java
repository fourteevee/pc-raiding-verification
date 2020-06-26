package commands.adminCommands;

import commands.Command;
import main.NestBot;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import punishment.Suspension;
import punishment.SuspensionHub;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Command that suspends a discord user from the raids.
 * Created by MistaCat 10/9/2018
 */
public class CommandReboot extends Command {
    public CommandReboot() {
        setAliases(new String[] {"reboot"});
        setMinRank(Rank.RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
        try {
            Runtime.getRuntime().exec("java -jar " + new java.io.File(NestBot.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath())
                    .getName());
            System.exit(0);
        } catch ( IOException e ) {
            e.printStackTrace();
            Utils.sendPM(msg.getAuthor(), "Failed to reboot! Please try again");
        }
    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Reboot");
        embedBuilder.addField("Required rank", "raids.Raid leader", false);
        embedBuilder.addField("Syntax", "-reboot", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Reboots the bot", false);
        return embedBuilder;
    }
}
