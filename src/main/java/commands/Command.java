package commands;

import lombok.Getter;
import lombok.Setter;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

/**
 * The base for a nest bot command. Over-ride execute to do as you want. Be sure to add commands to command hub.
 * Created by MistaCat 10/7/2018
 */
@Getter @Setter
public abstract class Command {
    private String[] aliases;
    private Rank minRank;

    public abstract void execute(Message msg, String alias, String[] args);

    public EmbedBuilder getInfo() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Failure!");
        embedBuilder.setDescription("This command has no information!");
        return embedBuilder;
    }
}
