package commands.adminCommands;

import commands.Command;
import main.Config;
import main.NestBot;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import utils.Utils;

import java.awt.*;

public class CommandVerifyMessage extends Command {
    public CommandVerifyMessage() {
        setAliases(new String[] {"verifymessage"});
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
        embedBuilder.setDescription("Please type -verify in this channel to receive instructions on how to verify!");
        embedBuilder.addField("REQUIREMENTS", "20 Stars\n2000 Alive Fame\n1 - 6/8 Class", true);
        Utils.sendEmbed(verifyChannel, embedBuilder);

    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Verify Message");
        embedBuilder.addField("Required rank", "Owner", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Sends verify message", false);
        return embedBuilder;
    }
}
