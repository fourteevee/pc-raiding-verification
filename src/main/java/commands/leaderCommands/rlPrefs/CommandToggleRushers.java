package commands.leaderCommands.rlPrefs;

import commands.Command;
import main.RaidLeaderPrefs;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import raids.Raid;
import raids.RaidHub;
import utils.Utils;


public class CommandToggleRushers extends Command {
    public CommandToggleRushers() {
        setAliases(new String[]{"rush", "rushers"});
        setMinRank(Rank.ALMOST_RL);
    }


    @Override
    public void execute(Message msg, String alias, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                Utils.sendPM(msg.getAuthor(), "Rushers will now recieve location");
                RaidLeaderPrefs.toggleRusher(msg.getAuthor().getId(), true);

            } else if (args[0].equalsIgnoreCase("off")) {
                Utils.sendPM(msg.getAuthor(), "Rushers will no longer recieve location");
                RaidLeaderPrefs.toggleRusher(msg.getAuthor().getId(), false);
            }
        } else if (args.length != 1) {
            Utils.sendPM(msg.getAuthor(), "Please use `-rush on` or `-rush off`");
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




