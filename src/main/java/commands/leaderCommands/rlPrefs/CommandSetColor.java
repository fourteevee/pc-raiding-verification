package commands.leaderCommands.rlPrefs;

import commands.Command;
import main.RaidLeaderPrefs;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import utils.Utils;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.Color.*;

public class CommandSetColor extends Command {
    public CommandSetColor() {
        setAliases(new String[]{"color"});
        setMinRank(Rank.ALMOST_RL);
    }

    public static boolean isValidHexaCode(String str)
    {
        // Regex to check valid hexadecimal color code.
        String regex = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the string is empty
        // return false
        if (str == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given string
        // and regular expression.
        Matcher m = p.matcher(str);

        // Return if the string
        // matched the ReGex
        return m.matches();
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
        if (args.length == 1) {
            if (isValidHexaCode(args[0]) == true) {
                Color AFKColor = Color.decode(args[0]);
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("`<- This color` is now the color of your AFK checks!")
                        .setColor(AFKColor);
                Utils.sendPMEmbed(msg.getAuthor(), embedBuilder);
                RaidLeaderPrefs.setColorPreference(msg.getAuthor().getId(), args[0]);

            } else {
                Utils.sendPM(msg.getAuthor(), "The color " + args[0] + " is not a valid color, please use colors in the hex format. For example: `#000000`");
            }
        }
    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Toggle Rushers");
        embedBuilder.addField("Required rank", "Almost raid leader.", false);
        embedBuilder.addField("Syntax", "-alias", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Use `-rush on` or `-rush off`. Toggles whether or not rushers recieve location for a specific raid leader", false);
        return embedBuilder;
    }
}
