package commands.miscCommands;

import main.NestBot;
import main.Rank;
import commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang.ArrayUtils;
import utils.Utils;


public class CommandCommands extends Command {
    public CommandCommands() {
        setAliases(new String[] {"commands"});
        setMinRank(Rank.DEFAULT);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
        if (args.length == 1) {
            if (NestBot.commands.getCommand(args[0]) == null) {
                Utils.sendPM(msg.getAuthor(), "Could not find that command!");
                return;
            }

            Utils.sendPMEmbed(msg.getAuthor(), NestBot.commands.getCommand(args[0]).getInfo());
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Commands")
                .setFooter("Use -commands <alias> to get more infos about a command.");
        String commands = "";

        for (Command cmd : NestBot.commands){
            if (Rank.getHighestRank(msg.getMember()).isAtLeast(cmd.getMinRank())){
                String aliases = "";
                for (String cmdAlias : cmd.getAliases()){
                    if (cmd.getAliases()[0] == cmdAlias) {
                        aliases = cmdAlias;
                    } else {
                        aliases = aliases + "/" + cmdAlias;
                    }
                }
                commands += "\n-" + aliases;
            }
        }
        embedBuilder.setDescription(commands);

        Utils.sendPMEmbed(msg.getAuthor(), embedBuilder);
    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Command information");
        embedBuilder.addField("Required rank", "Almost raid leader.", false);
        embedBuilder.addField("Syntax", "-alias <command alias>", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Shows information for the pest control bot commands", false);
        return embedBuilder;
}
}
