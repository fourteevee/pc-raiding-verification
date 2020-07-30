package listeners;

import commands.miscCommands.CommandExVerify;
import commands.miscCommands.CommandVerify;
import main.Constants;
import main.NestBot;
import main.Rank;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import moderation.CommandPunishmentHistory;
import moderation.CommandUserInfo;
import utils.Utils;

import java.util.Arrays;


public class CommandListener extends ListenerAdapter {

    private static final String COMMAND_PREFIX = "-";

    @Override
    public void onMessageReceived(MessageReceivedEvent event){

        if (event.getAuthor().isBot()) // if a bot ignore.
            return;

        String[] split = event.getMessage().getContentRaw().split(" ");
        String alias = split[0].replace(COMMAND_PREFIX, "");
        String[] args = Arrays.copyOfRange(split, 1, split.length);

        if (!event.getAuthor().isBot() && event.getChannel() == NestBot.getGuild().getTextChannelById(Constants.VERIFY_CHANNEL) && !(NestBot.commands.getCommand(alias) instanceof CommandVerify)) {
            event.getMessage().delete().queue();
            return;
        }

        if (!event.getAuthor().isBot() && event.getChannel() == NestBot.getGuild().getTextChannelById(Constants.EXVERIFY_CHANNEL) && !(NestBot.commands.getCommand(alias) instanceof CommandExVerify)) {
            event.getMessage().delete().queue();
            return;
        }

        if (!event.getMessage().getContentRaw().startsWith(COMMAND_PREFIX) && !(NestBot.commands.getCommand(alias) instanceof CommandVerify || NestBot.commands.getCommand(alias) instanceof CommandExVerify))
            return;

        if (NestBot.commands.getCommand(alias) != null) {
            if (event.getChannelType().equals(ChannelType.TEXT)) {
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

                if (NestBot.commands.getCommand(alias) instanceof CommandExVerify && (event.getChannel() != NestBot.getGuild().getTextChannelById(Constants.EXVERIFY_CHANNEL))) {
                    Utils.sendPM(event.getAuthor(), "Please verify in the verification channel!");
                    event.getMessage().delete().queue();
                    return;
                }

                if (NestBot.commands.getCommand(alias) instanceof CommandUserInfo || NestBot.commands.getCommand(alias) instanceof CommandPunishmentHistory) {
                    NestBot.commands.getCommand(alias).execute(event.getMessage(), alias, args);
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
                if (NestBot.commands.getCommand(alias) instanceof CommandVerify || NestBot.commands.getCommand(alias) instanceof CommandExVerify) {
                    NestBot.commands.getCommand(alias).execute(event.getMessage(), alias, args);
                }
                return;
            }
        } else {
            Utils.sendPM(event.getAuthor(), "Invalid command!");
            event.getMessage().delete().queue();
        }
    }

}
