package commands.leaderCommands.eventRaid;

import commands.Command;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.StringUtils;
import raids.Raid;
import raids.RaidHub;
import utils.Utils;

/**
 * Command to start a nest run. Only usable by raid leaders
 * Created by MistaCat 10/7/2018
 */
public class CommandEventStartRaid extends Command {
    public CommandEventStartRaid() {
        setAliases(new String[] {"eventraid", "eventnewraid"});
        setMinRank(Rank.RL);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
        if (RaidHub.isLeading(msg.getMember())) {
            Raid raid = RaidHub.getRaid(msg.getMember());
            if (!raid.isRaidActive()) {
                Utils.sendPM(msg.getAuthor(), "Your current raid is not in progress. So you can't finish and start a new one!");
                return;
            }

            if (!raid.isFinished() && raid.isRaidActive()) {
                if (args.length == 0) {
                    raid.initializeRaid();
                    return;
                }

                if (args.length == 1) {
                    try {
                        String location = args[0];
                        raid.setLocation(location, false);
                        raid.initializeRaid();
                        return;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Utils.sendPM(msg.getAuthor(), "Invalid raid location! Please retry");
                        return;
                    }
                }

                if (args.length == 2) {
                    try {
                        String location = args[0];
                        int size = Integer.parseInt(args[1]);
                        raid.setVoiceCap(size);
                        raid.setLocation(location, false);
                        raid.initializeRaid();
                        return;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Utils.sendPM(msg.getAuthor(), "Invalid raid size or location! Please retry");
                        return;
                    }
                }

                if (args.length == 3) {
                    try {
                        String location = args[0];
                        int size = Integer.parseInt(args[1]);
                        int time = Integer.parseInt(args[2]);
                        raid.setVoiceCap(size);
                        raid.setLocation(location, false);
                        raid.setTimer(time);
                        raid.initializeRaid();
                        return;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Utils.sendPM(msg.getAuthor(), "Invalid raid size, timer or location! Please retry");
                        return;
                    }
                }
            }

            if (args.length > 0 && StringUtils.isNumeric(args[0])) {
                raid.completeRaid(true, Integer.parseInt(args[0]));
                return;
            }

            raid.completeRaid(true, -1);
            return;
        }

        if (args.length == 1) {
            try {
                String location = args[0];
                RaidHub.startRaid(msg.getMember(), 0, -1, location, RaidHub.RaidType.EVENT_RAID);
                return;
            } catch (Exception ex) {
                Utils.sendPM(msg.getAuthor(), "Invalid raid location! Please retry");
                return;
            }
        }

        if (args.length == 2) {
            try {
                String location = args[0];
                int size = Integer.parseInt(args[1]);
                RaidHub.startRaid(msg.getMember(), size, -1, location, RaidHub.RaidType.EVENT_RAID);
                return;
            } catch (Exception ex) {
                Utils.sendPM(msg.getAuthor(), "Invalid raid size or location! Please retry");
                return;
            }
        }

        if (args.length == 3) {
            try {
                String location = args[0];
                int size = Integer.parseInt(args[1]);
                int time = Integer.parseInt(args[2]);
                RaidHub.startRaid(msg.getMember(), size, time, location, RaidHub.RaidType.EVENT_RAID);
                return;
            } catch (Exception ex) {
                Utils.sendPM(msg.getAuthor(), "Invalid raid size, timer or location! Please retry");
                return;
            }
        }

        RaidHub.startRaid(msg.getMember(), 0, -1, null, RaidHub.RaidType.EVENT_RAID);
    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Create event raid");
        embedBuilder.addField("Required rank", "Almost raid leader.", false);
        embedBuilder.addField("Syntax", "-alias <location> <room size> <countdown time (seconds)>", false);
        embedBuilder.addField("Syntax", "-alias <Players remaining>", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Creates a new raid room and pings people that a nest raid will be starting.\n" +
                "Can also re-open the room for another raid after one has begun with logging.", false);
        return embedBuilder;
}
}
