package listeners;

import commands.CommandHub;
import commands.miscCommands.CommandVerify;
import main.Config;
import main.Constants;
import main.NestBot;
import main.Rank;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.ArrayUtils;
import utils.Utils;

import java.util.Arrays;


public class CommandListener extends ListenerAdapter {

    private static final String COMMAND_PREFIX = "-";

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){

        if (!event.getAuthor().isBot() && event.getChannel() == NestBot.getGuild().getTextChannelById(Constants.VERIFY_CHANNEL) && !event.getMessage().getContentRaw().startsWith(COMMAND_PREFIX)) {
            event.getMessage().delete().queue();
            return;
        }

        if (!event.getAuthor().isBot() && event.getChannel() == NestBot.getGuild().getTextChannelById(Constants.EXVERIFY_CHANNEL) && !event.getMessage().getContentRaw().startsWith(COMMAND_PREFIX)) {
            event.getMessage().delete().queue();
            return;
        }

        if (event.getAuthor().isBot() || !event.getMessage().getContentRaw().startsWith(COMMAND_PREFIX)) // if a bot or not a Command ignore.
            return;

        String[] split = event.getMessage().getContentRaw().split(" ");
        String alias = split[0].replace(COMMAND_PREFIX, "");
        String[] args = Arrays.copyOfRange(split, 1, split.length);

        if (NestBot.commands.getCommand(alias) != null) {
            if (!Rank.getHighestRank(event.getMember()).isAtLeast(NestBot.commands.getCommand(alias).getMinRank()) && !Rank.overrideMembersId().contains(event.getMember().getId())) {
                Utils.sendPM(event.getAuthor(), "You don't have permission for this command!");
                event.getMessage().delete().queue();
                return;
            }

            if (NestBot.commands.getCommand(alias) instanceof CommandVerify && (event.getChannel() != NestBot.getGuild().getTextChannelById(Constants.VERIFY_CHANNEL))) {
                Utils.sendPM(event.getAuthor(), "Please verify in the verification channel!");
                event.getMessage().delete().queue();
                return;
            }

            if (NestBot.commands.getCommand(alias).getMinRank().isAtLeast(Rank.ALMOST_RL) && !Constants.RAID_COMMANDS.contains(Long.valueOf(event.getChannel().getIdLong())) &&
                    !Rank.getHighestRank(event.getMember()).isAtLeast(Rank.HEAD_RL)) {
                Utils.sendPM(event.getAuthor(), "Please use the raid leader commands channel!");
                event.getMessage().delete().queue();
                return;
            }

            NestBot.commands.getCommand(alias).execute(event.getMessage(), alias, args);

            if (event.getChannel() == NestBot.getGuild().getTextChannelById(Constants.VERIFY_CHANNEL))
                event.getMessage().delete().queue(m -> {}, e -> {});
        } else {
            Utils.sendPM(event.getAuthor(), "Invalid command!");
            event.getMessage().delete().queue();
        }
    }

}
