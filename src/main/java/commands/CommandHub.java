package commands;

import commands.miscCommands.CommandVerify;
import main.Constants;
import main.NestBot;
import main.Rank;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Manages all the commands sent to the discord
 * Created by MistaCat 10/7/2018
 */
public class CommandHub extends ArrayList<Command> {

    public Command getCommand(String alias) {
        return stream().filter(cmd -> Arrays.stream(cmd.getAliases()).anyMatch(s -> s.equalsIgnoreCase(alias))).findAny().orElse(null);
    }


}
