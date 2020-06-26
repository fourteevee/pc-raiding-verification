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

public class CommandReload extends Command {
    public CommandReload() {
        setAliases(new String[] {"reload"});
        setMinRank(Rank.OWNER);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
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
