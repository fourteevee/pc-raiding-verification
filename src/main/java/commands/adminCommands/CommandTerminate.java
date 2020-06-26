package commands.adminCommands;

import commands.Command;
import main.NestBot;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * Command that suspends a discord user from the raids.
 * Created by MistaCat 10/9/2018
 */
public class CommandTerminate extends Command {
    public CommandTerminate() {
        setAliases(new String[] {"terminate", "exit"});
        setMinRank(Rank.RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
        System.exit(0);
    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Terminate");
        embedBuilder.addField("Required rank", "raids.Raid leader", false);
        embedBuilder.addField("Syntax", "-alias", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Shuts down the bot", false);
        return embedBuilder;
}
}
