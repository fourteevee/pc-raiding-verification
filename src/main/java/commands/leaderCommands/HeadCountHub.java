package commands.leaderCommands;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;


import java.util.HashMap;

@Getter @Setter
public class HeadCountHub {

    public static HashMap<Member, Message> hcMembers = new HashMap<>();

}
