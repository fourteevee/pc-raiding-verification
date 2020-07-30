package main;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * An enum of all the pest control emotes.
 * Created by MistaCat 10/10/2018
 */
@Getter @AllArgsConstructor
public enum Emote {

    NEST(":Nest:", Long.parseLong((String) Config.get("NEST"))),
    DUNGEON(":Dungeon:", Long.parseLong((String) Config.get("DUNGEON"))),
    NEST_KEY(":NestKey:", Long.parseLong((String) Config.get("NEST_KEY"))),
    EVENT_KEY(":EventKey:", Long.parseLong((String) Config.get("EVENT_KEY"))),
    QOT(":QoT:", Long.parseLong((String) Config.get("QOT"))),
    RUSHER(":Zoomer:", Long.parseLong((String) Config.get("RUSHER"))),
    PRIEST(":Priest:", Long.parseLong((String) Config.get("PRIEST"))),
    PALLY(":Paladin:", Long.parseLong((String) Config.get("PALLY"))),
    WARRIOR(":Warrior:", Long.parseLong((String) Config.get("WARRIOR"))),
    KNIGHT(":Knight:", Long.parseLong((String) Config.get("KNIGHT"))),
    MYSTIC(":Mystic:", Long.parseLong((String) Config.get("MYSTIC"))),
    PURI(":Purification:", Long.parseLong((String) Config.get("PURI"))),
    SLOW(":Slow:", Long.parseLong((String) Config.get("SLOW"))),
    NITRO(":Nitro:", Long.parseLong((String) Config.get("NITRO"))),
    ASSIST(":Assist:", Long.parseLong((String) Config.get("ASSIST")));


    private String emoteName;
    private Long ID;


    public net.dv8tion.jda.api.entities.Emote getEmote() {
        return NestBot.getGuild().getJDA().getEmoteById(ID);
    }

    public String display() {
        return "<" + emoteName + ID + ">";
    }
}
