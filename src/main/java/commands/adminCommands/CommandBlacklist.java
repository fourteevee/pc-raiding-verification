package commands.adminCommands;

import commands.Command;
import main.Constants;
import main.NestBot;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import punishment.BlacklistManager;
import utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandBlacklist extends Command {
    public CommandBlacklist() {
        setAliases(new String[] {"blacklist"});
        setMinRank(Rank.OWNER);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
        if (args.length != 1) {
            Utils.sendPM(msg.getAuthor(), "Invalid amount of arguments!");
            return;
        }

        if (BlacklistManager.isBlacklisted(args[0])) {
            Utils.sendPM(msg.getAuthor(), "That user is already blacklisted!");
            return;
        }

        BlacklistManager.addToBlacklist(args[0]);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(args[0] + " has been blacklisted!");
        embedBuilder.setDescription("Blacklisted by: " + msg.getAuthor() +
                "\nDate: " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        Utils.sendEmbed(NestBot.getGuild().getTextChannelById(Constants.SUSPENSION_LOGS), embedBuilder);
    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Blacklist");
        embedBuilder.addField("Required rank", "Owner", false);
        embedBuilder.addField("Syntax", "-alias [realmeye name]", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Blacklists a realmeye name so that account cannot verify.", false);
        return embedBuilder;
    }
}
