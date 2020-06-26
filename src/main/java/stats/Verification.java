package stats;

import lombok.Getter;
import lombok.Setter;
import main.Constants;
import main.NestBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import utils.Utils;

import java.awt.*;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles all the verification of RotMG to discord accounts.
 * Created by MistaCat 10/7/2018
 */
public class Verification {
    @Getter private static Map<User, String> verificationRequests = new ConcurrentHashMap<>();
    @Getter @Setter private static int vericodes = 0;

    public static final String API_URL = "https://nightfirec.at/realmeye-api/?player=";
    public static final int STAR_REQ = 20;
    public static final int FAME_REQ = 2000;
    public static final int CLASS_REQ = 2;

    /**
     * Creates a verification request for a discord user.
     * @param user
     */
    public static String requestVerificationUser(User user) {
        String passcode = "PEST_" + getVericodes();
        getVerificationRequests().put(user, passcode);
        setVericodes(getVericodes() + 1);
        return passcode;
    }

    /**
     * Returns a Json string from a URL.
     * @param urlString
     * @return<<
     * @throws Exception
     */
    public static String readJsonURL(String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection con = url.openConnection();
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            String body = IOUtils.toString(in, encoding);

            return body;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a Json object for a realmeye username. username is not case sensitive.
     * @param username
     * @return
     */
    public static JSONObject getRealmPlayerObj(String username) {
        String json = readJsonURL(API_URL + username);

        if (json != null)
            return new JSONObject(json);
        else
            return null;
    }


    /**
     * Returns a RealmPlayer Object for a realmeye username. username is not case sensitive.
     * @param username
     * @return
     */
    public static RealmPlayer getRealmPlayer(String username) throws RealmPlayer.PrivateProfileException {
        JSONObject realmPlayerObj = getRealmPlayerObj(username);
        if (realmPlayerObj == null)
            return null;
        else
            return new RealmPlayer(username, getRealmPlayerObj(username));
    }

}
