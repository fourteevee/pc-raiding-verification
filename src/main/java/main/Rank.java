package main;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An enum that holds all the current Pest Control ranks
 * Created by MistaCAt 10/7/2018
 */
@Getter @AllArgsConstructor
public enum Rank {
    DEFAULT(null, false),
    VERIFIED(Long.parseLong((String) Config.get("VERIFIED")), false),
    TRIAL_RL(Long.parseLong((String) Config.get("TRIAL_RL")), true),
    ALMOST_RL(Long.parseLong((String) Config.get("ALMOST_RL")), true),
    RL(Long.parseLong((String) Config.get("RL")), true),
    EX_RL(Long.parseLong((String) Config.get("EX_RL")), true),
    SECURITY(Long.parseLong((String) Config.get("SECURITY")), true),
    OFFICER(Long.parseLong((String) Config.get("OFFICER")), true),
    HEAD_RL(Long.parseLong((String) Config.get("HEAD_RL")), true),
    ADMIN(Long.parseLong((String) Config.get("ADMIN")), true),
    OWNER(Long.parseLong((String) Config.get("OWNER")), true);

    private Long ID;
    private boolean canSpeak;

    public Role getRole() {
        return ID != null ? NestBot.getGuild().getRoleById(ID) : null;
    }

    public boolean isAtLeast(Rank other) {
        return ordinal() >= other.ordinal();
    }

    /**
     * Gets the highest rank if a user has one. Default if they dont have one.
     * @param member
     * @return
     */
    public static Rank getHighestRank(Member member) {
        for (Role role : member.getRoles()) {
            for (int i = Rank.OWNER.ordinal(); i > 0; i--) {
                if (role == Rank.values()[i].getRole())
                    return Rank.values()[i];
            }
        }

        return Rank.DEFAULT;
    }

    public static List<String> overrideMembersId(){
        List<String> membersId = new ArrayList<>();

        //returns the value from Config (e.g. 12321312234234, 2423423423444, 423423423423423)
        String configMembers = (String) Config.get("OVERRIDE_USERS");
        String[] splittedMembers = StringUtils.split(configMembers, ",");
        Arrays.stream(splittedMembers).forEach(s -> s.trim());

        membersId = Arrays.stream(splittedMembers).collect(Collectors.toList());

        return membersId;
    }
}
