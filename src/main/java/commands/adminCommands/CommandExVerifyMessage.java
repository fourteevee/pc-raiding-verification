package commands.adminCommands;

import commands.Command;
import main.NestBot;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import utils.Utils;

import java.awt.*;

public class CommandExVerifyMessage extends Command {
    public CommandExVerifyMessage() {
        setAliases(new String[] {"exverifymessage"});
        setMinRank(Rank.OWNER);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {

        TextChannel verifyChannel = msg.getTextChannel();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        try {
            verifyChannel.deleteMessages(verifyChannel.getHistory().retrievePast(99).complete()).complete();
        } catch ( Exception e ) {
            try {
                verifyChannel.deleteMessageById(verifyChannel.getLatestMessageId()).complete();
            } catch ( Exception exception ) {
            }
        }
        embedBuilder.setTitle("How to verify!");
        embedBuilder.setColor(new Color(0, 0, 255));
        embedBuilder.setThumbnail(NestBot.jda.retrieveApplicationInfo().complete().getIconUrl());
        embedBuilder.setDescription("Please type -exverify in this channel to receive instructions on how to verify!");
        embedBuilder.addField("REQUIREMENTS", "1 8/8 Melee \n" +
                "1 6/8 Any class", true);
        Utils.sendEmbed(verifyChannel, embedBuilder);

    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: ExVerify Message");
        embedBuilder.addField("Required rank", "Owner", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Sends exverify message", false);
        return embedBuilder;
    }
}
