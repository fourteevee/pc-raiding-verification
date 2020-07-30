package commands.leaderCommands;

import commands.Command;
import commands.leaderCommands.HeadCountHub;
import main.Constants;
import main.Emote;
import main.NestBot;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.StringUtils;
import utils.Utils;

/**
 * Command to start a nest run. Only usable by raid leaders
 * Created by MistaCat 10/7/2018
 */
public class CommandHeadcount extends Command {
    public CommandHeadcount() {
        setAliases(new String[] {"hc"});
        setMinRank(Rank.ALMOST_RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {

        Member member = msg.getMember();

        if (HeadCountHub.hcMembers.containsKey(member)) {
            Utils.sendPM(member.getUser(), "You already have an ongoing Head Count");
            return;
        }


        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(Utils.toAlphaNumeric(member.getEffectiveName()) + " HAS STARTED A HEAD COUNT", null, member.getUser().getAvatarUrl())
                .setDescription("Please react if you are interested in joining the raid, or if you have a key!");

        Message hcMessage = Utils.sendMessageWithEmbed(NestBot.getGuild().getTextChannelById(Constants.RAID_CHANNEL), "@here", embedBuilder);
        Utils.addReaction(hcMessage, Emote.NEST.getEmote());
        Utils.addReaction(hcMessage, Emote.NEST_KEY.getEmote());

        HeadCountHub.hcMembers.put(member, hcMessage);

    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Start a Head Count");
        embedBuilder.addField("Required rank", "Almost raid leader.", false);
        embedBuilder.addField("Syntax", "-alias", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Create a new Head Count.", false);
        return embedBuilder;
}
}
