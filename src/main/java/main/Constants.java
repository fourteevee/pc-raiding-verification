package main;

import java.util.List;

/**
 * A list of useful constants for the bot
 * Created by MistaCat 10/7/2018
 */
public class Constants {

    //Text channel ID's
    public static final Long VERIFY_CHANNEL =       Long.parseLong((String) Config.get("VERIFY_CHANNEL"));
    public static final Long EXVERIFY_CHANNEL =       Long.parseLong((String) Config.get("EXVERIFY_CHANNEL"));
    public static final Long RAID_CHANNEL =         Long.parseLong((String) Config.get("RAID_CHANNEL"));
    public static final Long EXRAID_CHANNEL =       Long.parseLong((String) Config.get("EXRAID_CHANNEL"));
    public static final Long EVENTRAID_CHANNEL =       Long.parseLong((String) Config.get("EVENTRAID_CHANNEL"));
    public static final Long WRRAID_CHANNEL =       Long.parseLong((String) Config.get("WRRAID_CHANNEL"));
    public static final List<Long> RAID_COMMANDS = Config.getList("RAID_COMMANDS");
    public static final Long FEEDBACK_LOGS =        Long.parseLong((String) Config.get("FEEDBACK_LOGS"));
    public static final Long SUSPENSION_LOGS =      Long.parseLong((String) Config.get("SUSPENSION_LOGS"));
    public static final Long RUN_LOGS =             Long.parseLong((String) Config.get("RUN_LOGS"));
    public static final Long VERIFICATION_LOGS =    Long.parseLong((String) Config.get("VERIFICATION_LOGS"));
    public static final Long RAID_LEADER_VOTE =     Long.parseLong((String) Config.get("RAID_LEADER_VOTE"));
    public static final Long LOOTS =                Long.parseLong((String) Config.get("LOOTS"));
    public static final Long VOTE_LOGS =             Long.parseLong((String) Config.get("VOTE_LOGS"));

    //Voice channel ID'sL
    public static final Long LOUNGE_VOICE =         Long.parseLong((String) Config.get("LOUNGE_VOICE"));

    //Category ID'sL

    /* Replaced RAID_CATEGORY with raidChannel.getParent();
    public static final Long RAID_CATEGORY =        Long.parseLong((String) Config.get("RAID_CATEGORY"));*/
    public static final Long FEEDBACK_CATEGORY =    Long.parseLong((String) Config.get("FEEDBACK_CATEGORY"));

    //Role ID'sL(String)
    public static final Long SUSPENDED =            Long.parseLong((String) Config.get("SUSPENDED"));
    public static final Long VERIFIED =             Long.parseLong((String) Config.get("VERIFIED"));
    public static final Long EXTERMINATOR  =        Long.parseLong((String) Config.get("EXTERMINATOR"));
    public static final Long WR_RAIDER  =        Long.parseLong((String) Config.get("WR_RAIDER"));
    public static final Long EVENT_RAIDER  =        Long.parseLong((String) Config.get("EVENT_RAIDER"));

}
