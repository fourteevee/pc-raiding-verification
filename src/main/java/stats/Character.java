package stats;

import lombok.Getter;

@Getter
public class Character {

    private final String username;
    private final String name;
    private final boolean backpack;
    private final long exp;
    private final long fame;
    private final long level;
    private final String pet;
    private final long stats_maxed;

    public Character(String username, String name, boolean backpack, long exp, long fame, long level, String pet, long stats_maxed) {
        this.username = username;
        this.name = name;
        this.backpack = backpack;
        this.exp = exp;
        this.fame = fame;
        this.level = level;
        this.pet = pet;
        this.stats_maxed = stats_maxed;
    }
}
