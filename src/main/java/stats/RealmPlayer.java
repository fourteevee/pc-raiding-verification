package stats;

import lombok.Getter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for the API
 */
@Getter
public class RealmPlayer {

    private final String username;
    private final long fame;
    private final long rank;
    private final List<Character> characters = new ArrayList<>();
    private final String description;

    public RealmPlayer(String username, JSONObject jsonObj) throws PrivateProfileException {

        if (jsonObj.keySet().contains("error")){
            throw new PrivateProfileException("Profile is set to private");
        }

        this.username = username;
        this.fame = jsonObj.getLong("fame");
        this.rank = jsonObj.getLong("rank");
        this.description = jsonObj.getString("desc1") + "\n" + jsonObj.getString("desc2") + "\n" + jsonObj.getString("desc3");

        for (Object characterObj : jsonObj.getJSONArray("characters")){

            if (characterObj instanceof JSONObject) {
                String name = ((JSONObject) characterObj).getString("class");
                boolean backpack = ((JSONObject) characterObj).getBoolean("backpack");
                long exp = ((JSONObject) characterObj).getLong("exp");
                long fame = ((JSONObject) characterObj).getLong("fame");
                long level = ((JSONObject) characterObj).getLong("level");
                String pet = ((JSONObject) characterObj).getString("pet");
                long stats_maxed = ((JSONObject) characterObj).getLong("stats_maxed");

                Character character = new Character(username, name, backpack, exp, fame, level, pet, stats_maxed);
                characters.add(character);
            }

        }
    }

    public static class PrivateProfileException extends Exception {
        public PrivateProfileException(String message) {
            super(message);
        }
    }

}
